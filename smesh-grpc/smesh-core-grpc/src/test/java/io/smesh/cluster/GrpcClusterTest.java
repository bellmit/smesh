package io.smesh.cluster;

import org.junit.Test;

import static io.smesh.cluster.ClusterState.*;

public class GrpcClusterTest {

    @Test
    public void shouldStartAndStopCluster() throws InterruptedException {
        final GrpcCluster cluster = newCluster("grpc-cluster-test");
        final ClusterStateCollector clusterStateCollector = new ClusterStateCollector<>(cluster);

        cluster.start();
        clusterStateCollector.verifyClusterStates(STOPPED, STARTING, STARTED);

        cluster.stop();
        clusterStateCollector.verifyClusterStates(STOPPED, STARTING, STARTED, STOPPING, STOPPED);
    }

    private GrpcCluster newCluster(final String localMemberName) {
        return new GrpcClusterBuilder()
                .withConfig(new GrpcClusterConfigBuilder()
                        .withLocalMemberName(localMemberName))
                .doBuild();
    }
}
