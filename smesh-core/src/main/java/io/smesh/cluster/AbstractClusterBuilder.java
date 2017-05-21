package io.smesh.cluster;


import java.util.Objects;

public abstract class AbstractClusterBuilder<C extends ClusterConfig, M extends ClusterMember, CB extends AbstractClusterConfigBuilder<C,CB>, B extends AbstractClusterBuilder<C,M,CB,B>> {

    protected C config;

    @SuppressWarnings("unchecked")
    public B withConfig(C config) {
        this.config = config;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B withConfig(CB builder) {
        this.config = builder.build();
        return (B) this;
    }


    public final Cluster<C,M> build() {
        if (config == null) {
            initConfig();
        }
        Objects.requireNonNull("config is required");
        return doBuild();
    }

    /**
     * Invoked if no config instance is available. Concrete implementations should
     * initialize a default configuration.
     */
    protected abstract void initConfig();

    protected abstract Cluster<C,M> doBuild();
}
