package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;

/**
 * Interface for task to be executed on the {@link io.smesh.cluster.task.TaskService.TaskThread#SCHEDULER} thread-pool.
 */
public interface ScheduledTask {
    void execute(Cluster cluster);
}
