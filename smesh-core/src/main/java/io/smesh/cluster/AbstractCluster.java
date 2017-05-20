package io.smesh.cluster;

import io.smesh.cluster.event.ClusterEventListener;
import io.smesh.cluster.event.ClusterStartedEvent;
import io.smesh.cluster.event.RemoteClusterMemberAddedEvent;
import io.smesh.cluster.lifecycle.ClusterLifecycleEvent;
import io.smesh.cluster.lifecycle.ClusterLifecycleListener;
import io.smesh.cluster.task.TaskService;
import io.smesh.cluster.task.TaskServiceImpl;
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

    private final ClusterConfig config;
    private volatile ClusterState state = ClusterState.STOPPED;
    private volatile Instant startTime;

    private final List<ClusterLifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();
    private final List<ClusterEventListener> eventListeners = new CopyOnWriteArrayList<>();
    private final List<ClusterMembershipListener> membershipListeners = new CopyOnWriteArrayList<>();

    private final TaskService taskService;

    private final RemoteMembers remoteMembers;
    private ClusterMember localMember;

    private final AtomicBoolean memberInfoMessageScheduled = new AtomicBoolean(false);


    public AbstractCluster(ClusterConfig config) {
        this.config = Objects.requireNonNull(config, "config is null");

        this.remoteMembers = new RemoteMembers(this);
        this.taskService = new TaskServiceImpl(this);

        if (config.isAutoStartup()) {
            this.start();
        }
    }

    @Override
    public ClusterConfig getConfig() {
        return config;
    }


    @Override
    public void start() {
        taskService.start();
        taskService.executeAwait(cluster -> {
            if (!isState(ClusterState.STOPPED)) {
                throw new IllegalStateException("Unable to start smesh cluster invalid state: " + getState());
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            LOGGER.info("Joining smesh cluster...");

            state = ClusterState.STARTING;
            triggerStateChanged(new ClusterLifecycleEvent(this, state));

            this.localMember = verifyLocalMember(doStart());

            startTime = Instant.now();
            state = ClusterState.STARTED;
            triggerStateChanged(new ClusterLifecycleEvent(this, state));

            scheduleLogMemberInfo();

            triggerEvent(new ClusterStartedEvent(this, remoteMembers.get(), getLocalMember(), startTime));

            stopWatch.stop();
            LOGGER.info("[{}]: Joining smesh cluster took: {}", localMember, stopWatch);

            return null;
        });
    }

    protected abstract ClusterMember doStart();

    @Override
    public void stop() {
        if (isState(ClusterState.STOPPED)) {
            return;
        }

        taskService.executeAwait(task -> {
            if (!isState(ClusterState.STARTED)) {
                String msg = String.format("Trying to stop smesh cluster with invalid state [%s]", state);
                LOGGER.warn(msg);
            }

            if (isState(ClusterState.STARTED, ClusterState.STARTING)) {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    LOGGER.info("[{}]: Stopping smesh cluster...", localMember);

                    state = ClusterState.STOPPING;
                    triggerStateChanged(new ClusterLifecycleEvent(this, state));

                    taskService.stop();

                    doStop();

                    remoteMembers.clear();
                    localMember = null;
                    startTime = null;
                    state = ClusterState.STOPPED;
                    triggerStateChanged(new ClusterLifecycleEvent(this, state));

                    stopWatch.stop();
                    LOGGER.info("Stopping smesh cluster took: {}", stopWatch);

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
        return remoteMembers.get();
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
        if (remoteMembers.add(remoteMember)) {
            LOGGER.debug("[{}]: added member [{}]", localMember, remoteMember);

            triggerRemoteMemberAdded(remoteMember);

            triggerEvent(new RemoteClusterMemberAddedEvent(this, remoteMembers.get(), remoteMember));

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
        if (remoteMembers.remove(remoteMember)) {
            LOGGER.debug("[{}]: removed remote member [{}]", getLocalMember(), remoteMember);

            //TODO: trigger event
            scheduleLogMemberInfo();
        }
    }

    @Override
    public TaskService getTaskService() {
        return taskService;
    }

    private static ClusterMember verifyLocalMember(ClusterMember member) {
        Objects.requireNonNull(member, "member is null");
        if (!member.isLocal()) {
            throw new IllegalArgumentException("Remote member not allowed");
        }
        return member;
    }


}
