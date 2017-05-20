package io.smesh.cluster;

public class HazelcastClusterMember extends ClusterMemberImpl {


    public HazelcastClusterMember(String name, String id, Role role, boolean local) {
        super(name, id, role, local);
    }
}
