package com.xiepuhuan.reptile;

import com.xiepuhuan.reptile.config.DeploymentModeEnum;
import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.consumer.CloseableConsumer;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.CloseableDownloader;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.CloseableResponseHandler;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.CloseableScheduler;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.utils.ArgUtils;
import com.xiepuhuan.reptile.utils.ObjectUtils;
import com.xiepuhuan.reptile.workflow.WorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.DistributedWorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.SingleWorkflowFactory;
import com.xiepuhuan.reptile.workflow.impl.WorkflowThreadFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
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
            return;
        }

        LOGGER.info("Reptile [{}] is stopping", reptileConfig.getName());
        executorService.shutdownNow();
        state.set(STOPPED);
        LOGGER.info("Reptile [{}] has stopped", reptileConfig.getName());
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
            close();
        } catch (InterruptedException e) {
            LOGGER.warn("Thread [{}] interrupted, crawler [{}] stopped", Thread.currentThread().getName(), reptileConfig.getName());
        }
    }

    //　关闭资源
    private void close() {
        CloseableScheduler closeableScheduler = getCloseableScheduler();
        CloseableDownloader closeableDownloader = getCloseableDownloader();
        CloseableConsumer closeableConsumer = getCloseableConsumer();
        List<CloseableResponseHandler> closeableResponseHandlers = getCloseableResponseHandler();

        try {
            if (Objects.nonNull(closeableScheduler)) {
                closeableScheduler.close();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(closeableScheduler),  e.getMessage());
        }

        try {
            if (Objects.nonNull(closeableDownloader)) {
                closeableDownloader.close();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(closeableDownloader), e.getMessage());
        }

        try {
            if (Objects.nonNull(closeableConsumer)) {
                closeableConsumer.close();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(closeableConsumer), e.getMessage());
        }

        if (CollectionUtils.isNotEmpty(closeableResponseHandlers)) {
            for (CloseableResponseHandler closeableResponseHandler : closeableResponseHandlers) {
                try {
                    closeableResponseHandler.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(closeableResponseHandler), e.getMessage());
                }
            }
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

    private CloseableScheduler getCloseableScheduler() {
        Scheduler scheduler = reptileConfig.getScheduler();
        return scheduler instanceof CloseableScheduler ? (CloseableScheduler) scheduler : null;
    }

    private CloseableDownloader getCloseableDownloader() {
        Downloader downloader = reptileConfig.getDownloader();
        return downloader instanceof CloseableDownloader ? (CloseableDownloader) downloader : null;
    }

    private CloseableConsumer getCloseableConsumer() {
        Consumer consumer = reptileConfig.getConsumer();
        return consumer instanceof CloseableConsumer ? (CloseableConsumer) consumer : null;
    }

    // 获取所有可关闭的ResponseHandler
    private List<CloseableResponseHandler> getCloseableResponseHandler() {
        List<ResponseHandler> responseHandlers = reptileConfig.getResponseHandlers();

        return responseHandlers.stream()
                .filter(CloseableResponseHandler.class::isInstance)
                .map(CloseableResponseHandler.class::cast)
                .collect(Collectors.toList());
    }
}
