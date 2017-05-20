package io.smesh.cluster;

public class MemoryClusterBuilder extends AbstractClusterBuilder<MemoryCluster, MemoryClusterBuilder> {

    @Override
    protected MemoryCluster doBuild() {
        return new MemoryCluster(config);
    }
}
