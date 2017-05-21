package io.smesh.cluster;

public class GrpcClusterMember extends ClusterMember {

    public GrpcClusterMember(String name, String id, Role role, boolean local) {
        super(name, id, role, local);
    }
}
