package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterMember;

import java.util.List;


public class RemoteClusterMemberAddedEvent extends ClusterEvent {

    public RemoteClusterMemberAddedEvent(Cluster cluster, List<ClusterMember> members, ClusterMember member) {
        super(cluster, members, member);
    }
}
