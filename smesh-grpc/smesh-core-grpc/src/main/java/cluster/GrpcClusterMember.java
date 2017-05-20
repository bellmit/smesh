package cluster;

import io.smesh.cluster.ClusterMemberImpl;

public class GrpcClusterMember extends ClusterMemberImpl {


    public GrpcClusterMember(String name, String uuid, Role role, boolean local) {
        super(name, uuid, role, local);
    }
}
