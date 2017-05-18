package io.smesh.cluster.lifecycle;

import io.smesh.cluster.ClusterState;

public final class ClusterLifecycleEvent {

    private final ClusterState state;

    public ClusterLifecycleEvent(ClusterState state) {
        this.state = state;
    }

    public ClusterState getState() {
        return state;
    }
}
