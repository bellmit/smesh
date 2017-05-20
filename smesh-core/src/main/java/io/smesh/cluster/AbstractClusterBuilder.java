package io.smesh.cluster;


import java.util.Collections;

public abstract class AbstractClusterBuilder<C extends Cluster, B extends AbstractClusterBuilder<C,B>> {

    protected ClusterConfig config;

    @SuppressWarnings("unchecked")
    public B withConfig(ClusterConfig config) {
        this.config = config;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B withConfig(ClusterConfigBuilder builder) {
        this.config = builder.build();
        return (B) this;
    }


    public final C build() {
        initConfig();
        return doBuild();
    }

    private void initConfig() {
        if (config == null) {
            config = new ClusterConfig(Collections.emptyMap());
        }
    }

    protected abstract C doBuild();
}
