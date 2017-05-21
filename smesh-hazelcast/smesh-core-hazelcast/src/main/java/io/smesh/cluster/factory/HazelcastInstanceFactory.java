package io.smesh.cluster.factory;

import com.hazelcast.core.HazelcastInstance;
import io.smesh.cluster.ClusterConfig;

public interface HazelcastInstanceFactory {

    HazelcastInstance create(ClusterConfig config);
}
