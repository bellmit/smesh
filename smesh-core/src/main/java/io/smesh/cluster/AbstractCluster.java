package io.smesh.cluster;

import io.smesh.cluster.event.ClusterEventListener;
import io.smesh.cluster.event.ClusterStartedEvent;
import io.smesh.cluster.event.RemoteClusterMemberAddedEvent;
import io.smesh.cluster.lifecycle.ClusterLifecycleEvent;
import io.smesh.cluster.lifecycle.ClusterLifecycleListener;
import io.smesh.cluster.task.TaskService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCluster implements Cluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCluster.class);

    private volatile ClusterState state = ClusterState.STOPPED;
    private volatile Instant startTime;

    private final List<ClusterLifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    private final List<ClusterEventListener> eventListeners = new CopyOnWriteArrayList<>();
    private final List<ClusterMembershipListener> membershipListeners = new CopyOnWriteArrayList<>();

    private final ClusterConfig config;
    private final TaskService taskService;

    private final List<ClusterMember> members = new CopyOnWriteArrayList<>();
    private ClusterMember localMember;

    private final AtomicBoolean memberInfoMessageScheduled = new AtomicBoolean(false);


    public AbstractCluster(ClusterConfig config, TaskService taskService) {
        this.config = Objects.requireNonNull(config);
        this.taskService = Objects.requireNonNull(taskService);

        taskService.setCluster(this);

        lifecycleListeners.add(0, event -> {
            if (event.getState() == ClusterState.STARTING) {
                doStart();
            } else if (event.getState() == ClusterState.STOPPING) {
                doStop();
            }
        });
    }

    @Override
    public ClusterConfig getConfig() {
        return config;
    }


    @Override
    public void start() {
        if (!config.isClusterEnabled()) {
            LOGGER.warn("Unable to start smesh cluster because it is disabled");
            return;
        }

        taskService.start();

        taskService.executeAwait(cluster -> {
            if (!isState(ClusterState.STOPPED)) {
                throw new IllegalStateException("Unable to start smesh cluster invalid state: " + getState());
            }

            state = ClusterState.STARTING;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            LOGGER.info("Joining smesh cluster...");

            triggerStateChanged(new ClusterLifecycleEvent(state));


            doStart();

            startTime = Instant.now();
            state = ClusterState.STARTED;
            triggerStateChanged(new ClusterLifecycleEvent(state));

            scheduleLogMemberInfo();

            triggerEvent(new ClusterStartedEvent(this, getRemoteMembersDirect(), getLocalMember(), startTime));

            stopWatch.stop();
            LOGGER.info("Joining smesh cluster took: {}", stopWatch);

            return null;
        });
    }

    protected void setLocalMember(ClusterMember member) {
        verifyLocalMember(member);
        this.localMember = member;
    }

    protected abstract void doStart();

    @Override
    public void stop() {
        if (isState(ClusterState.STOPPED)) {
            return;
        }

        taskService.executeAwait(task -> {
            if (!isState(ClusterState.STARTED)) {
                String msg = String.format("[%s]: Trying to stop smesh cluster with invalid state [%s]", getLocalMember(), state);
                LOGGER.warn(msg);
            }

            if (isState(ClusterState.STARTED, ClusterState.STARTING)) {
                try {
                    LOGGER.info("Stopping smesh cluster [{}]", getLocalMember());

                    state = ClusterState.STOPPING;
                    triggerStateChanged(new ClusterLifecycleEvent(state));

                    taskService.stop();

                    doStop();

                    members.clear();
                    startTime = null;
                    state = ClusterState.STOPPED;
                    triggerStateChanged(new ClusterLifecycleEvent(state));
                } catch (Exception e) {
                    String msg = String.format("Exception while stopping smesh cluster: %s", e.getMessage());
                    LOGGER.error(msg, e);
                }
            }
            return null;
        });
    }

    protected abstract void doStop();

    @Override
    public List<ClusterMember> getRemoteMembers() {
        return taskService.executeAwait(cluster -> getRemoteMembersDirect());
    }

    private List<ClusterMember> getRemoteMembersDirect() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public ClusterMember getLocalMember() {
        return localMember;
    }

    @Override
    public ClusterState getState() {
        return state;
    }

    @Override
    public boolean isState(ClusterState... statesToCheck) {
        if (ArrayUtils.isNotEmpty(statesToCheck)) {
            for (ClusterState stateToCheck : statesToCheck) {
                if (this.state == stateToCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void registerLifecycleListener(ClusterLifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    @Override
    public void unregisterLifecycleListener(ClusterLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    protected void scheduleLogMemberInfo() {
        if (memberInfoMessageScheduled.compareAndSet(false, true)) {
            taskService.schedule(cluster -> logMemberInfo(), 3, TimeUnit.SECONDS);
        }
    }

    private void logMemberInfo() {
        // TODO: implement
    }

    private void triggerStateChanged(ClusterLifecycleEvent event) {
        lifecycleListeners.forEach(listener -> listener.stateChanged(event));
    }

    private void triggerRemoteMemberAdded(ClusterMember remoteMember) {
        membershipListeners.forEach(listener -> listener.remoteMemberAdded(remoteMember));
    }

    private void triggerEvent(ClusterStartedEvent event) {
        taskService.event(cluster -> eventListeners.forEach(listener -> listener.localClusterStarted(event)));
    }

    private void triggerEvent(RemoteClusterMemberAddedEvent event) {
        taskService.event(cluster -> eventListeners.forEach(listener -> listener.memberAdded(event)));
    }

    @Override
    public void registerEventListener(ClusterEventListener eventListener) {
        if (!eventListeners.contains(eventListener)) {
            this.eventListeners.add(eventListener);

            // Notify late registered listeners when cluster is already started
            if (isState(ClusterState.STARTED)) {
                eventListener.localClusterStarted(new ClusterStartedEvent(this,
                        Collections.unmodifiableList(getRemoteMembers()), getLocalMember(), startTime));
            }
        }
    }

    @Override
    public void unregisterEventListener(ClusterEventListener eventListener) {
        this.eventListeners.remove(eventListener);
    }


    @Override
    public void addRemoteMember(ClusterMember remoteMember) {
        taskService.execute(cluster -> {
            doAddRemoteMember(remoteMember);
            return null;
        });
    }

    private void doAddRemoteMember(ClusterMember remoteMember) {
        verifyExecutingOnClusterThread();
        verifyRemoteMember(remoteMember);

        if (!isKnownMember(remoteMember)) {
            members.add(remoteMember);
            LOGGER.debug("[{}]: added member [{}]", localMember, remoteMember);

            triggerRemoteMemberAdded(remoteMember);

            List<ClusterMember> list = getRemoteMembersDirect();
            triggerEvent(new RemoteClusterMemberAddedEvent(this, list, remoteMember));

            scheduleLogMemberInfo();
        }
    }


    @Override
    public void removeRemoteMember(ClusterMember remoteMember) {
        taskService.execute(cluster -> {
            doRemoveRemoteMember(remoteMember);
            return null;
        });
    }

    private void doRemoveRemoteMember(ClusterMember remoteMember) {
        verifyExecutingOnClusterThread();
        verifyRemoteMember(remoteMember);

        if (isKnownMember(remoteMember)) {
            members.remove(remoteMember);
            LOGGER.debug("[{}]: removed remote member [{}]", getLocalMember(), remoteMember);

            //TODO: trigger event
            scheduleLogMemberInfo();
        }
    }

    protected TaskService getTaskService() {
        return taskService;
    }

    private boolean isKnownMember(ClusterMember member) {
        return members.contains(member);
    }

    private void verifyLocalMember(ClusterMember member) {
        Objects.requireNonNull(member);
        if (!member.isLocal()) {
            throw new IllegalArgumentException("Remote member not allowed");
        }
    }

    private void verifyRemoteMember(ClusterMember member) {
        Objects.requireNonNull(member);
        if (member.isLocal()) {
            throw new IllegalArgumentException("Local member not allowed");
        }
    }

    private void verifyExecutingOnClusterThread() {
        if (!taskService.executingOnThread(TaskService.TaskThread.CLUSTER)) {
            throw new UnsupportedOperationException("Execution only allowed on the [" + TaskService.TaskThread.CLUSTER.toString().toLowerCase() + "] cluster thread");
        }
    }



}
