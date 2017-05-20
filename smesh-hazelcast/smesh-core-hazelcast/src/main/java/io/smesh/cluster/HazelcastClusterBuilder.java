package io.smesh.cluster;

import com.hazelcast.core.HazelcastInstance;

public class HazelcastClusterBuilder extends AbstractClusterBuilder<HazelcastCluster, HazelcastClusterBuilder> {

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
            this.hazelcastInstanceFactory = new HazelcastInstanceFactoryImpl();
        }
    }
}
