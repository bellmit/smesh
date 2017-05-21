package io.smesh.cluster.factory.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigBuilder;
import com.hazelcast.config.XmlConfigBuilder;
import io.smesh.cluster.factory.HazelcastInstanceFactory;

import java.util.ArrayList;
import java.util.List;

public class HazelcastInstanceFactoryBuilder {

    private Config config;
    private List<HazelcastConfigCustomizer> customizers;

    public HazelcastInstanceFactoryBuilder withConfig(Config config) {
        this.config = config;
        return this;
    }

    public HazelcastInstanceFactoryBuilder withConfig(ConfigBuilder configBuilder) {
        return withConfig(configBuilder.build());
    }

    public HazelcastInstanceFactoryBuilder withCustomizer(HazelcastConfigCustomizer... toAdd) {
        if (customizers == null) {
            customizers = new ArrayList<>();
        }
        for (HazelcastConfigCustomizer hcc : toAdd) {
            customizers.add(hcc);
        }
        return this;
    }


    public HazelcastInstanceFactory build() {
        initConfig();
        return new HazelcastInstanceFactoryImpl(config, customizers);
    }

    protected void initConfig() {
        if (config == null) {
            this.config = new XmlConfigBuilder().build();
        }
    }
}
