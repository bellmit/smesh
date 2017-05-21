package io.smesh.cluster.factory.client;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import io.smesh.cluster.factory.HazelcastInstanceFactory;

import java.util.ArrayList;
import java.util.List;

public class HazelcastClientInstanceFactoryBuilder {

    private ClientConfig config;
    private List<HazelcastClientConfigCustomizer> customizers;

    public HazelcastClientInstanceFactoryBuilder withConfig(ClientConfig config) {
        this.config = config;
        return this;
    }

    public HazelcastClientInstanceFactoryBuilder withConfig(XmlClientConfigBuilder configBuilder) {
        return withConfig(configBuilder.build());
    }

    public HazelcastClientInstanceFactoryBuilder withCustomizer(HazelcastClientConfigCustomizer... toAdd) {
        if (customizers == null) {
            customizers = new ArrayList<>();
        }
        for (HazelcastClientConfigCustomizer hccc : toAdd) {
            customizers.add(hccc);
        }
        return this;
    }


    public HazelcastInstanceFactory build() {
        initConfig();
        return new HazelcastClientInstanceFactoryImpl(config, customizers);
    }

    protected void initConfig() {
        if (config == null) {
            this.config = new XmlClientConfigBuilder().build();
        }
    }
}
