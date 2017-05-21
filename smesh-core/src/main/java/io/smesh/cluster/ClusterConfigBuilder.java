package io.smesh.cluster;

public class ClusterConfigBuilder extends AbstractClusterConfigBuilder<ClusterConfig, ClusterConfigBuilder> {

    public ClusterConfig build() {
        return new ClusterConfig(properties);
    }
}
