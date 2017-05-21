package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

/**
 * Interface for task to be executed on the {@link io.smesh.cluster.task.TaskService.TaskThread#EVENT} thread-pool.
 */
public interface EventTask<C extends ClusterConfig, M extends ClusterMember> {
    void execute(Cluster<C,M> cluster);
}
