package io.smesh.cluster;

import com.hazelcast.core.HazelcastInstance;

public interface HazelcastInstanceFactory {

    HazelcastInstance create(ClusterConfig config);
}
