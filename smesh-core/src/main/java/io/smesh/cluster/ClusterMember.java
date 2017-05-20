package io.smesh.cluster;

import static io.smesh.cluster.ClusterMember.Role.CLIENT;
import static io.smesh.cluster.ClusterMember.Role.SERVER;

public interface ClusterMember {
    enum Role {
        CLIENT,
        SERVER;
    }

    String getName();

    String getUuid();

    boolean isLocal();

    Role getRole();

    default boolean isClient(Role role) {
        return getRole() == CLIENT;
    }

    default boolean isServer(Role role) {
        return getRole() == SERVER;
    }
}
