package io.smesh.cluster;

public class GrpcClusterMember extends ClusterMemberImpl {

    public GrpcClusterMember(String name, String id, Role role, boolean local) {
        super(name, id, role, local);
    }
}
