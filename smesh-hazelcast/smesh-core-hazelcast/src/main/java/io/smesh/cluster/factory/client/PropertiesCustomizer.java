package io.smesh.cluster.factory.client;

import com.hazelcast.client.config.ClientConfig;

/**
 * Customizer used for setting various hazelcast {@link ClientConfig} or {@link Config}
 * properties taken from the HazelcastCluster
 */
public class PropertiesCustomizer implements HazelcastClientConfigCustomizer {


    @Override
    public void customize(ClientConfig config) {

    }
}
