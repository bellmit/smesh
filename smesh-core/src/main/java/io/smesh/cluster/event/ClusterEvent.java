package io.smesh.cluster.event;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

import java.util.List;
import java.util.Objects;

public abstract class ClusterEvent<C extends ClusterConfig, M extends ClusterMember> {

    private final Cluster<C,M> cluster;
    private final List<M> members;
    private final M member;


    public ClusterEvent(Cluster<C,M> cluster, List<M> members, M member) {
        this.cluster = Objects.requireNonNull(cluster);
        this.members = Objects.requireNonNull(members);
        this.member = Objects.requireNonNull(member);
    }

    public M getMember() {
        return member;
    }

    public List<M> getMembers() {
        return members;
    }

    public Cluster<C,M> getCluster() {
        return cluster;
    }
}
