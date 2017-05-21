package io.smesh.cluster;

public class HazelcastClusterMember extends ClusterMember {


    public HazelcastClusterMember(String name, String id, Role role, boolean local) {
        super(name, id, role, local);
    }
}
