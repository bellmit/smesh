package io.smesh.cluster.factory.client;

import com.hazelcast.client.config.ClientConfig;

public interface HazelcastClientConfigCustomizer {

    void customize(ClientConfig config);
}
