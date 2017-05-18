package io.smesh.cluster;

public interface ClusterMember {

    String getUuid();

    String getName();

    boolean isLocal();
}
