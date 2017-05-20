package io.smesh.cluster;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastInstanceFactoryImpl implements HazelcastInstanceFactory {

    @Override
    public HazelcastInstance create(ClusterConfig config) {
        return Hazelcast.newHazelcastInstance(); // TODO:
    }
}
