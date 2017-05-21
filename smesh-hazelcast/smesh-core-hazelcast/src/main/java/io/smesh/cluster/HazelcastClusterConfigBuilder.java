package io.smesh.cluster;

public class HazelcastClusterConfigBuilder extends AbstractClusterConfigBuilder<HazelcastClusterConfig, HazelcastClusterConfigBuilder> {

    @Override
    public HazelcastClusterConfig build() {
        return new HazelcastClusterConfig(properties);
    }
}
