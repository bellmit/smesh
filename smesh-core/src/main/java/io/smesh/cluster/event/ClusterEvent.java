package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterMember;

import java.util.List;
import java.util.Objects;

public abstract class ClusterEvent {

    private final Cluster cluster;
    private final List<ClusterMember> members;
    private final ClusterMember member;


    public ClusterEvent(Cluster cluster, List<ClusterMember> members, ClusterMember member) {
        this.cluster = Objects.requireNonNull(cluster);
        this.members = Objects.requireNonNull(members);
        this.member = Objects.requireNonNull(member);
    }

    public ClusterMember getMember() {
        return member;
    }

    public List<ClusterMember> getMembers() {
        return members;
    }

    public Cluster getCluster() {
        return cluster;
    }
}
