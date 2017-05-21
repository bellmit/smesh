package io.smesh.cluster.lifecycle;

import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

public interface ClusterLifecycleListener<C extends ClusterConfig, M extends ClusterMember> {

    void stateChanged(ClusterLifecycleEvent<C,M> event);
}
