package io.smesh.cluster;


import io.smesh.cluster.task.TaskService;
import io.smesh.cluster.task.TaskServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.UUID;

public abstract class AbstractClusterBuilder<C extends Cluster, B extends AbstractClusterBuilder<C,B>> {

    protected ClusterConfig config;
    protected TaskService taskService;
    protected ClusterMember localMember;


    @SuppressWarnings("unchecked")
    public B withConfig(ClusterConfig config) {
        this.config = config;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B withConfig(ClusterConfigBuilder builder) {
        this.config = builder.build();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B withTaskService(TaskService taskService) {
        this.taskService = taskService;
        return (B) this;
    }


    public final C build() {
        handleConfig();
        handleTaskService();
        handleLocalMember();
        return doBuild();
    }

    private void handleConfig() {
        if (config == null) {
            config = new ClusterConfig(Collections.emptyMap());
        }
    }

    private void handleTaskService() {
        if (taskService == null) {
            taskService = new TaskServiceImpl();
        }
    }

    private void handleLocalMember() {
        if (localMember == null) {
            String localMemberName = config.getLocalMemberName();
            if (localMemberName == null) {
                localMemberName = RandomStringUtils.randomAlphabetic(10);
            }
            localMember = new ClusterMemberImpl(localMemberName, UUID.randomUUID().toString(), true);
        }
    }

    protected abstract C doBuild();
}
