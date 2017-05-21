package io.smesh.cluster;

import java.util.HashMap;
import java.util.Map;

import static io.smesh.cluster.ClusterConfig.CLUSTER_LOCAL_MEMBER_NAME;

public abstract class AbstractClusterConfigBuilder<C extends ClusterConfig, B extends AbstractClusterConfigBuilder> {

    protected Map<String,String> properties = new HashMap<>();

    @SuppressWarnings("unchecked")
    public B withProperties(Map<String,String> properties) {
        this.properties.putAll(properties);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B withProperty(String key, String value) {
        this.properties.put(key, value);
        return (B) this;
    }

    public B withLocalMemberName(String localMemberName) {
        return withProperty(CLUSTER_LOCAL_MEMBER_NAME.getName(), localMemberName);
    }

    public abstract C build();
}
