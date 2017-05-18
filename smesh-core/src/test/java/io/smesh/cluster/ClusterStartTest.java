package io.smesh.cluster;

import io.smesh.lifecycle.ClusterLifecycleEventExpectation;
import org.junit.Test;

import static io.smesh.cluster.ClusterTestUtils.testCluster;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClusterStartTest {

    @Test
    public void testStartEvents() throws InterruptedException {
        Cluster cluster = testCluster("member");
        ClusterLifecycleEventExpectation expectation = new ClusterLifecycleEventExpectation(2);
        cluster.registerLifecycleListener(expectation);
        cluster.start();

        expectation.await();

        assertTrue(cluster.isState(ClusterState.STARTED));
        assertEquals(2, expectation.getInvocations());
        assertEquals(ClusterState.STARTING, expectation.getEvents().get(0).getState());
        assertEquals(ClusterState.STARTED, expectation.getEvents().get(1).getState());
    }
}
