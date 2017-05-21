package io.smesh.cluster.lifecycle;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;
import io.smesh.cluster.ClusterState;

public final class ClusterLifecycleEvent<C extends ClusterConfig, M extends ClusterMember> {

    private final ClusterState state;
    private final Cluster<C,M> cluster;

    public ClusterLifecycleEvent(Cluster<C,M> cluster, ClusterState state) {
        this.cluster = cluster;
        this.state = state;
    }

    public Cluster<C,M> getCluster() {
        return cluster;
    }

    public ClusterState getState() {
        return state;
    }
}
