package io.smesh.cluster.event;


import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

public interface ClusterEventListener<C extends ClusterConfig, M extends ClusterMember> {

    void memberAdded(RemoteClusterMemberAddedEvent<C,M> event);

    void localClusterStarted(ClusterStartedEvent<C,M> event);
}
