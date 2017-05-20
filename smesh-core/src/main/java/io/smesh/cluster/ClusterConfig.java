package io.smesh.cluster;

import io.smesh.properties.SmeshProperties;
import io.smesh.properties.SmeshProperty;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.smesh.cluster.ClusterMember.Role;
import static io.smesh.cluster.ClusterMember.Role.SERVER;

public class ClusterConfig {

    public static final SmeshProperty CLUSTER_GROUP_NAME = new SmeshProperty("smesh.cluster.groupName", "smesh");
    public static final SmeshProperty CLUSTER_AUTO_STARTUP = new SmeshProperty("smesh.cluster.autoStartup", false);
    public static final SmeshProperty CLUSTER_LOCAL_MEMBER_NAME = new SmeshProperty("smesh.cluster.localMemberName");
    public static final SmeshProperty CLUSTER_LOCAL_MEMBER_ROLE = new SmeshProperty("smesh.cluster.localMemberRole", SERVER.toString());
    public static final SmeshProperty TASK_SERVICE_AWAIT_TERMINATION_SECONDS = new SmeshProperty("smesh.cluster.taskService.awaitTerminationSeconds", 0, TimeUnit.SECONDS);
    public static final SmeshProperty TASK_SERVICE_WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN = new SmeshProperty("smesh.cluster.taskService.waitForTasksToCompleteOnShutdown", false);

    private final SmeshProperties properties;

    public ClusterConfig(Properties nullableProperties) {
        this.properties = new SmeshProperties(nullableProperties);
    }

    public ClusterConfig(Map<String, String> nullableProperties) {
        this.properties = new SmeshProperties(nullableProperties);
    }

    public String getClusterGroup() {
        return properties.getString(CLUSTER_GROUP_NAME);
    }


    public String getLocalMemberName() {
        return properties.getString(CLUSTER_LOCAL_MEMBER_NAME);
    }

    public Role getLocalMemberRole() {
        return Role.valueOf(properties.getRequiredString(CLUSTER_LOCAL_MEMBER_ROLE).toUpperCase());
    }

    public int getTaskServiceAwaitTerminationSeconds() {
        return properties.getRequiredInteger(TASK_SERVICE_AWAIT_TERMINATION_SECONDS);
    }

    public boolean isTaskServiceWaitForTasksToCompleteOnShutdown() {
        return properties.getBoolean(TASK_SERVICE_WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN);
    }

    public boolean isAutoStartup() {
        return properties.getRequiredBoolean(CLUSTER_AUTO_STARTUP);
    }
}
