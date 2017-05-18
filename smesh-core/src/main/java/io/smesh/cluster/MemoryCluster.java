package io.smesh.cluster;


import io.smesh.cluster.task.TaskService;

public class MemoryCluster extends AbstractCluster {

    public MemoryCluster(ClusterConfig config, ClusterMember localMember, TaskService taskService) {
        super(config, localMember, taskService);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }
}
