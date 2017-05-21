package io.smesh.cluster.lifecycle;

import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.smesh.cluster.task.TaskService.TaskThread.CLUSTER;
import static org.junit.Assert.fail;

public class ClusterLifecycleEventExpectation<C extends ClusterConfig, M extends ClusterMember> implements ClusterLifecycleListener<C,M>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterLifecycleEventExpectation.class);

    private final List<ClusterLifecycleEvent<C,M>> events = new ArrayList<>();
    private int invocations;
    private CountDownLatch latch;

    public ClusterLifecycleEventExpectation(int expectedInvocations) {
        reset(expectedInvocations);
    }

    public void reset(int expectedInvocations) {
        invocations = 0;
        events.clear();
        latch = new CountDownLatch(expectedInvocations);
    }

    @Override
    public void stateChanged(ClusterLifecycleEvent<C,M> event) {
        LOGGER.info("Received lifecycle event: {}", event.getState());
        event.getCluster().getTaskService().verifyExecutingOnThread(CLUSTER);
        events.add(event);
        invocations += 1;
        latch.countDown();
    }

    public int getInvocations() {
        return invocations;
    }

    public List<ClusterLifecycleEvent<C,M>> getEvents() {
        return events;
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        boolean result = latch.await(timeout, unit);
        if (!result) {
            fail("didn't receive event within " + timeout + " " + unit);
        }
    }

    public void await() throws InterruptedException {
        await(10L, TimeUnit.SECONDS);
    }

}
