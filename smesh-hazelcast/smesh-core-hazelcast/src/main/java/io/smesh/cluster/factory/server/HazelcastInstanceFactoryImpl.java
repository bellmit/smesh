package io.smesh.cluster.factory.server;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.factory.HazelcastInstanceFactory;

import java.util.List;
import java.util.Objects;

public class HazelcastInstanceFactoryImpl implements HazelcastInstanceFactory {

    private final Config hazelcastConfig;
    private final List<HazelcastConfigCustomizer> customizers;

    public HazelcastInstanceFactoryImpl(Config hazelcastConfig) {
        this(hazelcastConfig, null);
    }

    public HazelcastInstanceFactoryImpl(Config hazelcastConfig, List<HazelcastConfigCustomizer> customizers) {
        this.hazelcastConfig = Objects.requireNonNull(hazelcastConfig, "hazelcastConfig is required");
        this.customizers = customizers;
    }

    @Override
    public HazelcastInstance create(ClusterConfig config) {
        if (customizers != null) {
            customizers.forEach(customizer -> customizer.customize(hazelcastConfig));
        }
        return Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
}
