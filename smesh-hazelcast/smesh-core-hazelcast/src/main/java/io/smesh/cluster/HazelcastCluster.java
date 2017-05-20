package io.smesh.cluster;

import com.hazelcast.core.HazelcastInstance;

import java.util.Objects;

public class HazelcastCluster extends AbstractCluster {

    // hazelcast instance may be set via constructor directly, in which case we don't touch...
    // when not set then the factory must be set and a hazelcastInstance will be create during startup
    private final boolean hazelcastInstanceExternallyManaged;
    private HazelcastInstance hazelcastInstance;
    private HazelcastInstanceFactory hazelcastInstanceFactory;

    public HazelcastCluster(ClusterConfig config, HazelcastInstance hazelcastInstance) {
        super(config);
        this.hazelcastInstance = Objects.requireNonNull(hazelcastInstance);
        this.hazelcastInstanceExternallyManaged = true;
    }

    public HazelcastCluster(ClusterConfig config, HazelcastInstanceFactory hazelcastInstanceFactory) {
        super(config);
        this.hazelcastInstanceFactory = Objects.requireNonNull(hazelcastInstanceFactory);
        this.hazelcastInstanceExternallyManaged = false;
    }

    @Override
    protected ClusterMember doStart() {
        if (!hazelcastInstanceExternallyManaged) {
            this.hazelcastInstance = hazelcastInstanceFactory.create(getConfig());
        }
        // TODO:
        return null;
    }

    @Override
    protected void doStop() {
        if (!hazelcastInstanceExternallyManaged) {
            this.hazelcastInstance.shutdown();
            this.hazelcastInstance = null;
        }
    }
}
