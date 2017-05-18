package io.smesh.cluster;

public enum ClusterState {
    STARTING(true),
    STARTED(true),
    STOPPING(false),
    STOPPED(false);

    private final boolean active;

    ClusterState(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
