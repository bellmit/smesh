package io.smesh.cluster;

public class MemoryClusterBuilder extends AbstractClusterBuilder<ClusterConfig, ClusterMember, ClusterConfigBuilder, MemoryClusterBuilder> {

    @Override
    protected void initConfig() {
        withConfig(new ClusterConfigBuilder());
    }

    @Override
    protected MemoryCluster doBuild() {
        return new MemoryCluster(config);
    }
}
