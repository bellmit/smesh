package io.smesh.cluster.event;


public interface ClusterEventListener {

    void memberAdded(RemoteClusterMemberAddedEvent event);

    void localClusterStarted(ClusterStartedEvent event);
}
