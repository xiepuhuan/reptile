package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.impl.ResponseHandlerChain;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.impl.AbstractFilterScheduler;
import com.xiepuhuan.reptile.workflow.Workflow;

/**
 * @author xiepuhuan
 */
public abstract class AbstractWorkflow implements Workflow {

    private final String name;

    private final Scheduler scheduler;

    private final Downloader downloader;

    private final ResponseHandlerChain responseHandlerChain;

    private final Consumer consumer;

    private final long sleepTime;

    private final int retryCount;

    private final AbstractFilterScheduler filterScheduler;

    public AbstractWorkflow(String name, ReptileConfig config) {
        this.name = name;
        this.scheduler = config.getScheduler();
        this.downloader = config.getDownloader();
        this.responseHandlerChain = config.getResponseHandlerChain();
        this.consumer = config.getConsumer();
        this.sleepTime = config.getSleepTime();
        this.retryCount = config.getRetryCount();
        this.filterScheduler = scheduler instanceof  AbstractFilterScheduler ? (AbstractFilterScheduler) scheduler : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public AbstractFilterScheduler getFilterScheduler() {
        return filterScheduler;
    }

    @Override
    public Downloader getDownloader() {
        return downloader;
    }

    @Override
    public ResponseHandlerChain getResponseHandlerChain() {
        return responseHandlerChain;
    }

    @Override
    public Consumer getConsumer() {
        return consumer;
    }

    @Override
    public long getSleepTime() {
        return sleepTime;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }
}
