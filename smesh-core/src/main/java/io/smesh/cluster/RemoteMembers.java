package io.smesh.cluster;

import io.smesh.cluster.task.TaskService.TaskThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class RemoteMembers<M extends ClusterMember> {

    private final List<M> members = new ArrayList<>();
    private final Cluster cluster;

    RemoteMembers(Cluster cluster) {
        this.cluster = cluster;
    }

    void clear() {
        cluster.getTaskService().verifyExecutingOnThread(TaskThread.CLUSTER);
        members.clear();
    }

    boolean add(M member) {
        verifyRemoteMember(member);
        cluster.getTaskService().verifyExecutingOnThread(TaskThread.CLUSTER);
        return members.add(member);
    }

    boolean remove(M member) {
        verifyRemoteMember(member);
        cluster.getTaskService().verifyExecutingOnThread(TaskThread.CLUSTER);
        return members.remove(member);
    }

    List<M> get() {
        if (cluster.getTaskService().executingOnThread(TaskThread.CLUSTER)) {
            return Collections.unmodifiableList(members);
        }
        return Collections.unmodifiableList(new ArrayList<>(members));
    }

    private static void verifyRemoteMember(ClusterMember member) {
        Objects.requireNonNull(member, "member is null");
        if (member.isLocal()) {
            throw new IllegalArgumentException("Local member not allowed");
        }
    }
}
