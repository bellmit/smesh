package io.smesh.cluster;

import com.hazelcast.core.HazelcastInstance;
import io.smesh.cluster.ClusterMember.Role;
import io.smesh.cluster.factory.client.HazelcastClientInstanceFactoryBuilder;
import io.smesh.cluster.factory.HazelcastInstanceFactory;
import io.smesh.cluster.factory.server.HazelcastInstanceFactoryBuilder;

public class HazelcastClusterBuilder extends AbstractClusterBuilder<HazelcastClusterConfig, HazelcastClusterMember, HazelcastClusterConfigBuilder, HazelcastClusterBuilder> {

    private HazelcastInstance hazelcastInstance;
    private HazelcastInstanceFactory hazelcastInstanceFactory;

    public HazelcastClusterBuilder withHazelcastInstance(HazelcastInstance hazelcastInstance) {
        if (hazelcastInstanceFactory != null) {
            throw new IllegalArgumentException("hazelcastInstanceFactory already set");
        }
        this.hazelcastInstance = hazelcastInstance;
        return this;
    }

    public HazelcastClusterBuilder withHazelcastInstanceFactory(HazelcastInstanceFactory hazelcastInstanceFactory) {
        if (hazelcastInstance != null) {
            throw new IllegalArgumentException("hazelcastInstanceFactory already set");
        }
        this.hazelcastInstanceFactory = hazelcastInstanceFactory;
        return this;
    }

    public HazelcastClusterBuilder withHazelcastInstanceFactory(HazelcastInstanceFactoryBuilder hazelcastInstanceFactoryBuilder) {
        return withHazelcastInstanceFactory(hazelcastInstanceFactoryBuilder.build());
    }

    @Override
    protected HazelcastCluster doBuild() {
        initHazelcastInstanceFactory();
        if (hazelcastInstance == null) {
            return new HazelcastCluster(config, hazelcastInstanceFactory);
        }
        return new HazelcastCluster(config, hazelcastInstance);
    }

    protected void initHazelcastInstanceFactory() {
        if (hazelcastInstance == null && hazelcastInstanceFactory == null) {
            if (config.getLocalMemberRole() == Role.SERVER) {
                this.hazelcastInstanceFactory = new HazelcastInstanceFactoryBuilder().build();
            } else {
                this.hazelcastInstanceFactory = new HazelcastClientInstanceFactoryBuilder().build();
            }
        }
    }

    @Override
    protected void initConfig() {
        if (config == null) {
            withConfig(new HazelcastClusterConfigBuilder());
        }
    }
}
