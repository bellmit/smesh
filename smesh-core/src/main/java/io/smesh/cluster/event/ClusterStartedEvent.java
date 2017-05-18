package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterMember;

import java.time.Instant;
import java.util.List;


public class ClusterStartedEvent extends ClusterEvent {

    private final Instant startTime;

    public ClusterStartedEvent(Cluster cluster, List<ClusterMember> members, ClusterMember member, Instant startTime) {
        super(cluster, members, member);
        this.startTime = startTime;
    }

    public Instant getStartTime() {
        return startTime;
    }
}
