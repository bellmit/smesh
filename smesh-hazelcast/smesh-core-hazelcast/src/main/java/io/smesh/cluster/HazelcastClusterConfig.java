package io.smesh.cluster;

import io.smesh.cluster.ClusterConfig;

import java.util.Map;
import java.util.Properties;

public class HazelcastClusterConfig extends ClusterConfig {

    public HazelcastClusterConfig(Properties nullableProperties) {
        super(nullableProperties);
    }

    public HazelcastClusterConfig(Map<String, String> nullableProperties) {
        super(nullableProperties);
    }
}
