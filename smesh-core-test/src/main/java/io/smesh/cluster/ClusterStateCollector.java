package io.smesh.cluster;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ClusterStateCollector<C extends ClusterConfig, M extends ClusterMember> {

    private final Cluster<C,M> cluster;
    private final ClusterState initialClusterState;
    private final Collection<ClusterState> collectedClusterStates = new ArrayList<>();

    public ClusterStateCollector(final Cluster<C,M> cluster) {
        this.cluster = Objects.requireNonNull(cluster);
        initialClusterState = cluster.getState();
        cluster.registerLifecycleListener(event -> collectedClusterStates.add(event.getState()));
    }

    public void verifyClusterStates(ClusterState initialClusterState, ClusterState... registeredStates) {
        assertEquals("Initial cluster state did not match.",
                initialClusterState, this.initialClusterState);

        assertEquals("Amount of collected states did not match",
                registeredStates.length, collectedClusterStates.size());

        final Iterator<ClusterState> stateIterator = collectedClusterStates.iterator();
        for (int i = 0; i < registeredStates.length; i++) {
            assertEquals("Cluster state #" + (i + 1) + " did not match",
                    registeredStates[i], stateIterator.next());
        }
    }

    public Cluster<C,M> getCluster() {
        return cluster;
    }

    public ClusterState getInitialClusterState() {
        return initialClusterState;
    }

    public Collection<ClusterState> getCollectedClusterStates() {
        return Collections.unmodifiableCollection(collectedClusterStates);
    }
}
