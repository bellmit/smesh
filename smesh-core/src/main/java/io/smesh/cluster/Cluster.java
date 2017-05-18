package io.smesh.cluster;

import io.smesh.cluster.event.ClusterEventListener;
import io.smesh.cluster.lifecycle.ClusterLifecycleListener;
import io.smesh.cluster.task.TaskService;

import java.util.List;

public interface Cluster {

    void start();

    void stop();

    ClusterState getState();

    ClusterConfig getConfig();

    boolean isState(ClusterState... state);

    void registerLifecycleListener(ClusterLifecycleListener listener);

    void unregisterLifecycleListener(ClusterLifecycleListener listener);

    void registerEventListener(ClusterEventListener eventListener);

    void unregisterEventListener(ClusterEventListener eventListener);

    List<ClusterMember> getRemoteMembers();

    ClusterMember getLocalMember();

    void removeRemoteMember(ClusterMember remoteMember);

    void addRemoteMember(ClusterMember remoteMember);

    TaskService getTaskService();
}
