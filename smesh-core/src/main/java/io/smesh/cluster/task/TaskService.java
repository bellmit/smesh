package io.smesh.cluster.task;

import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * The task-service manages three thread-pools:
 *
 * <ul>
 *     <li>CLUSTER: This pool runs single threaded and executes code that change {@link io.smesh.cluster.Cluster} instance's state.</li>
 *     <li>EVENT: This pool runs single threaded and executes {@link io.smesh.cluster.event.ClusterEventListener} code.</li>
 *     <li>SCHEDULER: This pool uses two threads and is used for scheduling various tasks.</li>
 * </ul>
 */
public interface TaskService<C extends ClusterConfig, M extends ClusterMember> {

    enum TaskThread {
        CLUSTER,
        EVENT,
        NOTIFIER
    }

    void start();

    void stop();

    boolean executingOnThread(TaskThread thread);

    void verifyExecutingOnThread(TaskThread thread);

    void scheduleWithFixedDelay(ScheduledTask<C,M> task, long initialDelay, long delay, TimeUnit timeUnit);

    void schedule(ScheduledTask<C,M> task, long delay, TimeUnit timeUnit);

    <R> TaskCall<R> execute(ClusterTask<C,M,R> task);

    void event(EventTask<C,M> task);

    <R> R executeAwait(ClusterTask<C,M,R> task);

    abstract class TaskCall<R> implements Callable<R> {

        private static final Logger LOGGER = LoggerFactory.getLogger(TaskCall.class);

        @Override
        public final R call() {
            try {
                return doCall();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }

        protected abstract R doCall();
    }
}
