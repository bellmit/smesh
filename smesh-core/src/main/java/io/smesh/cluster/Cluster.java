package io.smesh.cluster;

import io.smesh.cluster.event.ClusterEventListener;
import io.smesh.cluster.lifecycle.ClusterLifecycleListener;
import io.smesh.cluster.task.TaskService;

import java.util.List;

public interface Cluster<C extends ClusterConfig, M extends ClusterMember> {

    void start();

    void stop();

    ClusterState getState();

    C getConfig();

    boolean isState(ClusterState... state);

    void registerLifecycleListener(ClusterLifecycleListener<C,M> listener);

    void unregisterLifecycleListener(ClusterLifecycleListener<C,M> listener);

    void registerEventListener(ClusterEventListener<C,M> eventListener);

    void unregisterEventListener(ClusterEventListener<C,M> eventListener);

    List<M> getRemoteMembers();

    M getLocalMember();

    void removeRemoteMember(M remoteMember);

    void addRemoteMember(M remoteMember);

    TaskService getTaskService();
}
