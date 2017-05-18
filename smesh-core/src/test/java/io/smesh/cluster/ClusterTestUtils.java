package io.smesh.cluster;

import io.smesh.cluster.task.TaskServiceImpl;

import java.util.Collections;
import java.util.UUID;

public class ClusterTestUtils {

    public static Cluster testCluster(String localMemberName) {
        return new MemoryCluster(new ClusterConfig(Collections.emptyMap()),
                new ClusterMemberImpl(localMemberName, UUID.randomUUID().toString(), true), new TaskServiceImpl());
    }
}
