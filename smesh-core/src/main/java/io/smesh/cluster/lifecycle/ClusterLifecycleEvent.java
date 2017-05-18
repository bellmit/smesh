package io.smesh.cluster.lifecycle;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterState;

public final class ClusterLifecycleEvent {

    private final ClusterState state;
    private final Cluster cluster;

    public ClusterLifecycleEvent(Cluster cluster, ClusterState state) {
        this.cluster = cluster;
        this.state = state;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public ClusterState getState() {
        return state;
    }
}
