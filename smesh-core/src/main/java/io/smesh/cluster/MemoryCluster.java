package io.smesh.cluster;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;
import java.util.UUID;

public class MemoryCluster extends AbstractCluster<ClusterConfig, ClusterMember> {

    public MemoryCluster(ClusterConfig config) {
        super(config);
    }

    @Override
    protected ClusterMember doStart() {
        final String name =  Optional.ofNullable(getConfig().getLocalMemberName()).orElse(RandomStringUtils.randomAlphabetic(10));
        return new ClusterMember(name, UUID.randomUUID().toString(), getConfig().getLocalMemberRole(), true);
    }

    @Override
    protected void doStop() {

    }
}
