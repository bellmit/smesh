package io.smesh.cluster;

public class GrpcClusterConfigBuilder extends AbstractClusterConfigBuilder<GrpcClusterConfig, GrpcClusterConfigBuilder> {

    @Override
    public GrpcClusterConfig build() {
        return new GrpcClusterConfig(properties);
    }
}
