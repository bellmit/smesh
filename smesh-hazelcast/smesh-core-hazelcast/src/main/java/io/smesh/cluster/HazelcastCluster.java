package io.smesh.cluster;

import com.hazelcast.core.HazelcastInstance;
import io.smesh.cluster.factory.HazelcastInstanceFactory;

import java.util.Objects;

public class HazelcastCluster extends AbstractCluster<HazelcastClusterConfig, HazelcastClusterMember> {

    // hazelcast instance may be set via constructor directly, in which case we don't touch...
    // when not set then the factory must be set and a hazelcastInstance will be create during startup
    private final boolean hazelcastInstanceExternallyManaged;
    private HazelcastInstance hazelcastInstance;
    private HazelcastInstanceFactory hazelcastInstanceFactory;

    public HazelcastCluster(HazelcastClusterConfig config, HazelcastInstance hazelcastInstance) {
        super(config);
        this.hazelcastInstance = Objects.requireNonNull(hazelcastInstance);
        this.hazelcastInstanceExternallyManaged = true;
    }

    public HazelcastCluster(HazelcastClusterConfig config, HazelcastInstanceFactory hazelcastInstanceFactory) {
        super(config);
        this.hazelcastInstanceFactory = Objects.requireNonNull(hazelcastInstanceFactory);
        this.hazelcastInstanceExternallyManaged = false;
    }

    @Override
    protected HazelcastClusterMember doStart() {
        if (!hazelcastInstanceExternallyManaged) {
            this.hazelcastInstance = hazelcastInstanceFactory.create(getConfig());
        }
        // validate if we have correct hazelcast instance (client vs server)
        System.out.println(hazelcastInstance.getClass());



        return new HazelcastClusterMember("x", "y", getConfig().getLocalMemberRole(), true);
    }

    @Override
    protected void doStop() {
        if (!hazelcastInstanceExternallyManaged) {
            this.hazelcastInstance.shutdown();
            this.hazelcastInstance = null;
        }
    }
}
