package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

import java.util.List;


public class RemoteClusterMemberAddedEvent<C extends ClusterConfig, M extends ClusterMember> extends ClusterEvent<C,M> {

    public RemoteClusterMemberAddedEvent(Cluster<C,M> cluster, List<M> members, M member) {
        super(cluster, members, member);
    }
}
