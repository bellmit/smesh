package io.smesh.cluster;

public class HazelcastClusterMember extends ClusterMemberImpl {


    public HazelcastClusterMember(String name, String uuid, Role role, boolean local) {
        super(name, uuid, role, local);
    }
}
