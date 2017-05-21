package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

import java.time.Instant;
import java.util.List;


public class ClusterStartedEvent<C extends ClusterConfig, M extends ClusterMember> extends ClusterEvent<C,M> {

    private final Instant startTime;

    public ClusterStartedEvent(Cluster<C,M> cluster, List<M> members, M member, Instant startTime) {
        super(cluster, members, member);
        this.startTime = startTime;
    }

    public Instant getStartTime() {
        return startTime;
    }
}
