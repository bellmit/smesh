package io.smesh.cluster.lifecycle;

public interface ClusterLifecycleListener {

    void stateChanged(ClusterLifecycleEvent event);
}
