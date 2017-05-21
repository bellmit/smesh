package io.smesh.cluster;

import java.util.Map;
import java.util.Properties;

public class GrpcClusterConfig extends ClusterConfig {

    public GrpcClusterConfig(Properties nullableProperties) {
        super(nullableProperties);
    }

    public GrpcClusterConfig(Map<String, String> nullableProperties) {
        super(nullableProperties);
    }
}
