package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;

/**
 * Interface for task to be executed on the {@link io.smesh.cluster.task.TaskService.TaskThread#EVENT} thread-pool.
 */
public interface EventTask {
    void execute(Cluster cluster);
}
