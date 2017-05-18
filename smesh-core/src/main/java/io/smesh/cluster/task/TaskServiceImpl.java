package io.smesh.cluster.task;

import io.smesh.cluster.Cluster;
import io.smesh.cluster.ClusterAware;
import io.smesh.cluster.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class TaskServiceImpl implements TaskService, ClusterAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final ThreadLocal<TaskService.TaskThread> TL = new ThreadLocal<>();

    private Cluster cluster;
    private ThreadPoolExecutor clusterThreadPool;
    private ThreadPoolExecutor eventThreadPool;
    private ScheduledThreadPoolExecutor schedulerThreadPool;


    @Override
    public void start() {
        clusterThreadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new NamedThreadFactory("smesh-cluster-"));

        eventThreadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new NamedThreadFactory("smesh-notifier-"));

        schedulerThreadPool = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("smesh-scheduler-"));
        schedulerThreadPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        schedulerThreadPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        schedulerThreadPool.setRemoveOnCancelPolicy(true);
    }

    @Override
    public void stop() {
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
    public <R> TaskCall<R> execute(ClusterTask<R> task) {
        ClusterThreadRunner<R> runner = newRunner(task);
        clusterThreadPool.execute(runner);
        return runner;
    }

    @Override
    public <R> R executeAwait(ClusterTask<R> task) {
        if (executingOnThread(TaskThread.CLUSTER)) {
            return task.invoke(cluster);
        } else {
            return execute(task).call();
        }
    }

    @Override
    public void event(EventTask task) {
        eventThreadPool.execute(newRunner(task));
    }

    @Override
    public void schedule(ScheduledTask task, long delay, TimeUnit timeUnit) {
        schedulerThreadPool.schedule(newRunner(task), delay, timeUnit);
    }

    @Override
    public void scheduleWithFixedDelay(ScheduledTask task, long initialDelay, long delay, TimeUnit timeUnit) {
        schedulerThreadPool.scheduleWithFixedDelay(newRunner(task), initialDelay, delay, timeUnit);
    }

    @Override
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }


    private SchedulerThreadRunner newRunner(ScheduledTask task) {
        return new SchedulerThreadRunner(cluster, task);
    }

    private EventThreadRunner newRunner(EventTask task) {
        return new EventThreadRunner(cluster, task);
    }

    private <R> ClusterThreadRunner<R> newRunner(ClusterTask<R> task) {
        return new ClusterThreadRunner<>(cluster, task);
    }


    private static class ClusterThreadRunner<R> extends TaskCall<R> implements Callable<R>, Runnable {

        private final CountDownLatch latch = new CountDownLatch(1);

        private R result = null;
        private boolean resultSet = false;

        private final Cluster cluster;
        private final ClusterTask<R> task;

        private ClusterThreadRunner(Cluster cluster, ClusterTask<R> task) {
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


    private static class EventThreadRunner implements Runnable {
        private final Cluster cluster;
        private final EventTask task;

        private EventThreadRunner(Cluster cluster, EventTask task) {
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

    private static class SchedulerThreadRunner implements Runnable {
        private final Cluster cluster;
        private final ScheduledTask task;

        private SchedulerThreadRunner(Cluster cluster, ScheduledTask task) {
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
