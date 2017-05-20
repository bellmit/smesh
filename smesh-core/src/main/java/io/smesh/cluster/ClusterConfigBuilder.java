package io.smesh.cluster;


import java.util.HashMap;
import java.util.Map;

import static io.smesh.cluster.ClusterConfig.CLUSTER_LOCAL_MEMBER_NAME;

public class ClusterConfigBuilder {

    private Map<String,String> properties = new HashMap<>();

    public ClusterConfigBuilder withProperties(Map<String,String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ClusterConfigBuilder withProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public ClusterConfigBuilder withLocalMemberName(String localMemberName) {
        return withProperty(CLUSTER_LOCAL_MEMBER_NAME.getName(), localMemberName);
    }

    public ClusterConfig build() {
        return new ClusterConfig(properties);
    }
}
