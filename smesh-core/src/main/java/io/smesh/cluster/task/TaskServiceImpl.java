package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;
import io.smesh.cluster.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

public class TaskServiceImpl<C extends ClusterConfig, M extends ClusterMember> implements TaskService<C,M> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final ThreadLocal<TaskService.TaskThread> TL = new ThreadLocal<>();

    private final Cluster<C,M> cluster;
    private ThreadPoolExecutor clusterThreadPool;
    private ThreadPoolExecutor eventThreadPool;
    private ScheduledThreadPoolExecutor schedulerThreadPool;
    private volatile boolean started = false;

    public TaskServiceImpl(Cluster<C,M> cluster) {
        this.cluster = Objects.requireNonNull(cluster, "cluster is required");
    }

    @Override
    public void start() {
        if (!started) {
            clusterThreadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                    new NamedThreadFactory("smesh-cluster-"));

            eventThreadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                    new NamedThreadFactory("smesh-notifier-"));

            schedulerThreadPool = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("smesh-scheduler-"));
            schedulerThreadPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            schedulerThreadPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            schedulerThreadPool.setRemoveOnCancelPolicy(true);
            started = true;
        }
    }

    @Override
    public void stop() {
        if (started) {
            try {
                doShutdown(schedulerThreadPool);
                doShutdown(clusterThreadPool);
                doShutdown(eventThreadPool);

                doAwaitTermination(schedulerThreadPool, "scheduler");
                doAwaitTermination(clusterThreadPool, "executor");
                doAwaitTermination(eventThreadPool, "notifier");

                schedulerThreadPool = null;
                clusterThreadPool = null;
                eventThreadPool = null;
                started = false;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void doAwaitTermination(ThreadPoolExecutor executorService, String name) {
        if (executorService == null) {
            return;
        }

        int seconds = cluster.getConfig().getTaskServiceAwaitTerminationSeconds();
        if (seconds > 0) {
            try {
                if (!executorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
                    LOGGER.warn("Timed out while waiting for executor [{}] to terminate", name);
                }
            } catch (InterruptedException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
    }

    private void doShutdown(ThreadPoolExecutor executorService) {
        if (executorService == null) {
            return;
        }

        if (cluster.getConfig().isTaskServiceWaitForTasksToCompleteOnShutdown()) {
            executorService.shutdown();
        } else {
            executorService.shutdownNow();
        }
    }

    @Override
    public boolean executingOnThread(TaskThread thread) {
        TaskThread taskThread = TL.get();
        return taskThread != null && taskThread == thread;
    }

    @Override
    public void verifyExecutingOnThread(TaskThread thread) {
        if (!executingOnThread(thread)) {
            throw new IllegalStateException("Execution only allowed on the [" + thread + "] cluster thread, but executing on " + Thread.currentThread().getName());
        }
    }

    @Override
    public <R> TaskCall<R> execute(ClusterTask<C,M,R> task) {
        ClusterThreadRunner<C,M,R> runner = newRunner(task);
        clusterThreadPool.execute(runner);
        return runner;
    }

    @Override
    public <R> R executeAwait(ClusterTask<C,M,R> task) {
        if (executingOnThread(TaskThread.CLUSTER)) {
            return task.invoke(cluster);
        } else {
            return execute(task).call();
        }
    }

    @Override
    public void event(EventTask<C,M> task) {
        eventThreadPool.execute(newRunner(task));
    }

    @Override
    public void schedule(ScheduledTask<C,M> task, long delay, TimeUnit timeUnit) {
        schedulerThreadPool.schedule(newRunner(task), delay, timeUnit);
    }

    @Override
    public void scheduleWithFixedDelay(ScheduledTask<C,M> task, long initialDelay, long delay, TimeUnit timeUnit) {
        schedulerThreadPool.scheduleWithFixedDelay(newRunner(task), initialDelay, delay, timeUnit);
    }

    private SchedulerThreadRunner<C,M> newRunner(ScheduledTask<C,M> task) {
        return new SchedulerThreadRunner<>(cluster, task);
    }

    private EventThreadRunner<C,M> newRunner(EventTask<C,M> task) {
        return new EventThreadRunner<>(cluster, task);
    }

    private <R> ClusterThreadRunner<C,M,R> newRunner(ClusterTask<C,M,R> task) {
        return new ClusterThreadRunner<>(cluster, task);
    }


    private static class ClusterThreadRunner<C extends ClusterConfig,M extends ClusterMember,R> extends TaskCall<R> implements Callable<R>, Runnable {

        private final CountDownLatch latch = new CountDownLatch(1);

        private R result = null;
        private boolean resultSet = false;

        private final Cluster<C,M> cluster;
        private final ClusterTask<C,M,R> task;

        private ClusterThreadRunner(Cluster<C,M> cluster, ClusterTask<C,M,R> task) {
            this.cluster = cluster;
            this.task = task;
        }

        @Override
        public final void run() {
            try {
                TL.set(TaskThread.CLUSTER);
                try {
                    R result = task.invoke(cluster);
                    setResult(result);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } finally {
                if (!resultSet) {
                    setResult(null);
                }
                TL.remove();
            }
        }


        @Override
        protected R doCall() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return result;
        }

        protected void setResult(R result) {
            this.result = result;
            this.resultSet = true;
            this.latch.countDown();
        }
    }


    private static class EventThreadRunner<C extends ClusterConfig,M extends ClusterMember> implements Runnable {
        private final Cluster<C,M> cluster;
        private final EventTask<C,M> task;

        private EventThreadRunner(Cluster<C,M> cluster, EventTask<C,M> task) {
            this.cluster = cluster;
            this.task = task;
        }


        @Override
        public final void run() {
            try {
                TL.set(TaskThread.NOTIFIER);
                task.execute(cluster);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                TL.remove();
            }
        }
    }

    private static class SchedulerThreadRunner<C extends ClusterConfig,M extends ClusterMember> implements Runnable {
        private final Cluster<C,M> cluster;
        private final ScheduledTask<C,M> task;

        private SchedulerThreadRunner(Cluster<C,M> cluster, ScheduledTask<C,M> task) {
            this.cluster = cluster;
            this.task = task;
        }


        @Override
        public final void run() {
            try {
                TL.set(TaskThread.EVENT);
                task.execute(cluster);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                TL.remove();
            }
        }
    }

}
