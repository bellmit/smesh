package io.smesh.cluster.factory.server;

import com.hazelcast.config.Config;

public interface HazelcastConfigCustomizer {

    void customize(Config config);
}
