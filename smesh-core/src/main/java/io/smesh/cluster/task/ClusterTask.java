package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

/**
 * Interface for task to be executed on the {@link io.smesh.cluster.task.TaskService.TaskThread#CLUSTER} thread-pool.
 */
public interface ClusterTask<C extends ClusterConfig, M extends ClusterMember, R> {
    R invoke(Cluster<C,M> cluster);
}
