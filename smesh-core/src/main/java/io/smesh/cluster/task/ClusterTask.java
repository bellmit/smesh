package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;

/**
 * Interface for task to be executed on the {@link io.smesh.cluster.task.TaskService.TaskThread#CLUSTER} thread-pool.
 */
public interface ClusterTask<R> {
    R invoke(Cluster cluster);
}
