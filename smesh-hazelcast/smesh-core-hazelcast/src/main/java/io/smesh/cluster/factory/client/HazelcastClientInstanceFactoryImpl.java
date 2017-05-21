package io.smesh.cluster.factory.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.factory.HazelcastInstanceFactory;

import java.util.List;
import java.util.Objects;

public class HazelcastClientInstanceFactoryImpl implements HazelcastInstanceFactory {

    private final ClientConfig hazelcastClientConfig;
    private final List<HazelcastClientConfigCustomizer> customizers;

    public HazelcastClientInstanceFactoryImpl(ClientConfig hazelcastClientConfig) {
        this(hazelcastClientConfig, null);
    }

    public HazelcastClientInstanceFactoryImpl(ClientConfig hazelcastClientConfig, List<HazelcastClientConfigCustomizer> customizers) {
        this.hazelcastClientConfig = Objects.requireNonNull(hazelcastClientConfig, "hazelcastClientConfig is required");
        this.customizers = customizers;
    }

    @Override
    public HazelcastInstance create(ClusterConfig config) {
        if (customizers != null) {
            customizers.forEach(customizer -> customizer.customize(hazelcastClientConfig));
        }
        return HazelcastClient.newHazelcastClient(hazelcastClientConfig);
    }
}