package com.xiepuhuan.reptile;

import com.xiepuhuan.reptile.config.DeploymentModeEnum;
import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.CloseableScheduler;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.utils.ArgUtils;
import com.xiepuhuan.reptile.workflow.WorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.DistributedWorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.SingleWorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.WorkflowThreadFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */

public class Reptile implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reptile.class);

    private static final int RUNNING = 1;

    private static final int STOPPED = 0;

    private static final int STOP = -1;

    private ReptileConfig reptileConfig;

    private Thread thread;

    private AtomicInteger activeThreadCount;

    private CountDownLatch finalization;

    private ExecutorService executorService;

    private AtomicInteger state;

    private Reptile(ReptileConfig reptileConfig) {
        ArgUtils.notNull(reptileConfig, "reptileConfig");
        reptileConfig.check();
        this.reptileConfig = reptileConfig;
        this.executorService = buildExecutor();
        this.state = new AtomicInteger(STOPPED);
        this.finalization = new CountDownLatch(reptileConfig.getThreadCount());
        this.activeThreadCount = new AtomicInteger(0);

    }

    public static Reptile create(ReptileConfig reptileConfig) {
        return new Reptile(reptileConfig);
    }

    private ExecutorService buildExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(reptileConfig.getThreadCount(),
                reptileConfig.getThreadCount(), 1L, TimeUnit.NANOSECONDS,
                new SynchronousQueue<>(), new WorkflowThreadFactory(reptileConfig.getName()));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public void start() {
        if (!state.compareAndSet(STOPPED, RUNNING)) {
            LOGGER.error("Reptile [{}] is already running", reptileConfig.getName());
        }

        if (reptileConfig.isAsynRun()) {
            thread = new Thread(this, reptileConfig.getName() + "-thread");
            thread.start();
        } else {
            thread = Thread.currentThread();
            thread.setName(reptileConfig.getName() + "-thread");
            run();
        }
    }


    public void stop() {
        if (!state.compareAndSet(RUNNING, STOP)) {
            LOGGER.error("Reptile [{}] has stopped", reptileConfig.getName());
        }

        LOGGER.info("Reptile [{}] is stopping", reptileConfig.getName());
        close();
        state.set(STOPPED);
        LOGGER.info("Reptile [{}] has stopped", reptileConfig.getName());
    }

    private void close() {
        executorService.shutdownNow();
    }

    @Override
    public void run() {
        if (state.get() != RUNNING) {
            throw new IllegalStateException("The current state is not running");
        }

        LOGGER.info("Reptile [{}] start up", reptileConfig.getName());

        WorkflowFactory workflowFactory = buildWorkflow(reptileConfig);
        for (int i = 0; i < reptileConfig.getThreadCount(); ++i) {
            executorService.execute(workflowFactory.newWorkflow());
        }
        try {
            finalization.await();
            reptileConfig.getDownloader().close();
            Scheduler scheduler = reptileConfig.getScheduler();
            if (scheduler instanceof CloseableScheduler) {
                ((CloseableScheduler) scheduler).close();
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Thread [{}] interrupted, crawler [{}] stopped", Thread.currentThread().getName(), reptileConfig.getName());
        }
    }

    public Reptile addUrls(String... urls) {
        reptileConfig.getScheduler().push(Arrays.stream(urls).map(Request::new).toArray(Request[]::new));
        return this;
    }

    public Reptile addRequests(Request... requests) {
        reptileConfig.getScheduler().push(requests);
        return this;
    }

    public Reptile addRequests(Collection<Request> requests) {
        reptileConfig.getScheduler().push(requests);
        return this;
    }

    public Reptile addUrls(Collection<String> urls) {
        reptileConfig.getScheduler().push(urls.stream().map(Request::new).collect(Collectors.toList()));
        return this;
    }

    public int getActiveThreadCount() {
        return activeThreadCount.get();
    }


    private WorkflowFactory buildWorkflow(ReptileConfig reptileConfig) {
        return reptileConfig.getDeploymentMode() == DeploymentModeEnum.SINGLE ?
                new SingleWorkflowFactory(finalization, reptileConfig) :
                new DistributedWorkflowFactory(finalization, reptileConfig);
    }

}
