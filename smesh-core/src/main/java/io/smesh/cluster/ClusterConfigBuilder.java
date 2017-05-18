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

    public ClusterConfigBuilder withLocalMemberName(String localMemberName) {
        this.properties.put(CLUSTER_LOCAL_MEMBER_NAME.getName(), localMemberName);
        return this;
    }

    public ClusterConfig build() {
        return new ClusterConfig(properties);
    }
}
