package com.xiepuhuan.reptile;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.constants.DeploymentModeEnum;
import com.xiepuhuan.reptile.consumer.CloseableConsumer;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.CloseableDownloader;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.CloseableResponseHandler;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.handler.impl.ResponseHandlerChain;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 * 爬虫主体
 */

public class Reptile implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reptile.class);

    // 爬虫正在启动中
    private static final int STARTING = 2;

    // 爬虫处于运行状态
    private static final int RUNNING = 1;

    // 爬虫处于停止状态
    private static final int STOPPED = 0;

    // 爬虫正在停止
    private static final int STOPPING = -1;

    // 爬虫配置类
    private ReptileConfig reptileConfig;

    // 爬虫主线程，管理工作流线程的生命周期
    private Thread thread;

    // 爬虫活跃的工作流线程
    private AtomicInteger activeThreadCount;

    // 爬虫状态
    private AtomicInteger state;

    // 爬虫工作流线程池
    private ExecutorService executorService;

    private CountDownLatch finalization;

    private Reptile(ReptileConfig reptileConfig) {
        ArgUtils.notNull(reptileConfig, "reptileConfig");
        reptileConfig.check();
        this.reptileConfig = reptileConfig;
        this.executorService = buildExecutor();
        this.state = new AtomicInteger(STOPPED);
        this.finalization = new CountDownLatch(getThreadCount());
        this.activeThreadCount = new AtomicInteger(0);

    }

    public static Reptile create(ReptileConfig reptileConfig) {
        return new Reptile(reptileConfig);
    }

    public void start() {
        if (!state.compareAndSet(STOPPED, STARTING)) {
            LOGGER.error("Reptile [{}] has started", getName());
            return;
        }

        LOGGER.info("Reptile [{}] Startup", getName());
        if (isAsynRun()) {
            thread = new Thread(this, getName() + "-thread");
            thread.start();
        } else {
            thread = Thread.currentThread();
            thread.setName(getName() + "-thread");
            run();
        }
    }

    public void stop() {
        if (!state.compareAndSet(RUNNING, STOPPING) && !state.compareAndSet(STARTING, STOPPING)) {
            LOGGER.error("Reptile [{}] has stopped", getName());
            return;
        }

        LOGGER.info("Reptile [{}] is stopping", getName());
        executorService.shutdownNow();
        state.compareAndSet(STOPPING, STOPPED);
        LOGGER.info("Reptile [{}] has stopped", getName());
    }

    @Override
    public void run() {
        if (state.get() != STARTING) {
            throw new IllegalStateException("The current state is not starting");
        }

        LOGGER.info("Reptile [{}] is running", getName());

        WorkflowFactory workflowFactory = buildWorkflow(reptileConfig);
        for (int i = 0; i < getThreadCount(); ++i) {
            executorService.execute(workflowFactory.newWorkflow());
        }
        state.compareAndSet(STARTING, RUNNING);
        try {
            finalization.await();
            close();
        } catch (InterruptedException e) {
            LOGGER.warn("Thread [{}] interrupted, crawler [{}] stopped", Thread.currentThread().getName(), getName());
        }
    }

    public Reptile addUrls(String... urls) {
        getScheduler().push(Arrays.stream(urls).map(Request::new).toArray(Request[]::new));
        return this;
    }

    public Reptile addUrls(Collection<String> urls) {
        getScheduler().push(urls.stream().map(Request::new).collect(Collectors.toList()));
        return this;
    }

    public Reptile addRequests(Request... requests) {
        getScheduler().push(requests);
        return this;
    }

    public Reptile addRequests(Collection<Request> requests) {
        getScheduler().push(requests);
        return this;
    }

    public int getActiveThreadCount() {
        return activeThreadCount.get();
    }

    public String getName() {
        return reptileConfig.getName();
    }

    public int getThreadCount() {
        return reptileConfig.getThreadCount();
    }

    public Scheduler getScheduler() {
        return reptileConfig.getScheduler();
    }

    public Downloader getDownloader() {
        return reptileConfig.getDownloader();
    }

    public Consumer getConsumer() {
        return reptileConfig.getConsumer();
    }

    public ResponseHandlerChain getResponseHandlerChain() {
        return reptileConfig.getResponseHandlerChain();
    }

    public boolean isAsynRun() {
        return reptileConfig.isAsynRun();
    }

    //　关闭爬虫的四大组件资源
    private void close() {
        CloseableScheduler closeableScheduler = getCloseableScheduler();
        CloseableDownloader closeableDownloader = getCloseableDownloader();
        CloseableConsumer closeableConsumer = getCloseableConsumer();

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

        try {
            getResponseHandlerChain().close();
        } catch (IOException e) {
            LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(getResponseHandlerChain()), e.getMessage());
        }
    }

    private ExecutorService buildExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadCount(),
                getThreadCount(), 1L, TimeUnit.NANOSECONDS,
                new SynchronousQueue<>(), new WorkflowThreadFactory(getName()));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private WorkflowFactory buildWorkflow(ReptileConfig reptileConfig) {
        return reptileConfig.getDeploymentMode() == DeploymentModeEnum.SINGLE ?
                new SingleWorkflowFactory(finalization, reptileConfig) :
                new DistributedWorkflowFactory(finalization, reptileConfig);
    }

    private CloseableScheduler getCloseableScheduler() {
        Scheduler scheduler = getScheduler();
        return scheduler instanceof CloseableScheduler ? (CloseableScheduler) scheduler : null;
    }

    private CloseableDownloader getCloseableDownloader() {
        Downloader downloader = getDownloader();
        return downloader instanceof CloseableDownloader ? (CloseableDownloader) downloader : null;
    }

    private CloseableConsumer getCloseableConsumer() {
        Consumer consumer = getConsumer();
        return consumer instanceof CloseableConsumer ? (CloseableConsumer) consumer : null;
    }
}
