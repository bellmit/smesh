package io.smesh.cluster.lifecycle;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterState;
import io.smesh.cluster.MemoryClusterBuilder;
import io.smesh.cluster.lifecycle.ClusterLifecycleEventExpectation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClusterLifecycleEventsTest {
//TODO: event tests + taskservice tests
    @Test
    public void triggersStartAndStopEvents() throws InterruptedException {
        Cluster cluster = new MemoryClusterBuilder().build();

        ClusterLifecycleEventExpectation expectation = new ClusterLifecycleEventExpectation(2);
        cluster.registerLifecycleListener(expectation);
        cluster.start();
        expectation.await();

        assertTrue(cluster.isState(ClusterState.STARTED));
        assertEquals(2, expectation.getInvocations());
        assertEquals(ClusterState.STARTING, expectation.getEvents().get(0).getState());
        assertEquals(ClusterState.STARTED, expectation.getEvents().get(1).getState());

        expectation.reset(2);
        cluster.stop();
        expectation.await();

        assertTrue(cluster.isState(ClusterState.STOPPED));
        assertEquals(2, expectation.getInvocations());
        assertEquals(ClusterState.STOPPING, expectation.getEvents().get(0).getState());
        assertEquals(ClusterState.STOPPED, expectation.getEvents().get(1).getState());
    }

    @Test
    public void shouldNoLongerReceiveEventAfterUnregister() throws InterruptedException {
        Cluster cluster = new MemoryClusterBuilder().build();

        ClusterLifecycleEventExpectation expectation = new ClusterLifecycleEventExpectation(2);
        cluster.registerLifecycleListener(expectation);
        cluster.start();
        expectation.await();

        assertEquals(2, expectation.getInvocations());
        expectation.reset(0);
        cluster.unregisterLifecycleListener(expectation);

        cluster.stop();
        expectation.await();
        assertEquals(0, expectation.getInvocations());

    }
}
