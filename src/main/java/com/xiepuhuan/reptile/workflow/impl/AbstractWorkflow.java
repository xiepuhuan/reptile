package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.impl.AbstractFilterScheduler;
import com.xiepuhuan.reptile.workflow.Workflow;
import java.util.List;

/**
 * @author xiepuhuan
 */
public abstract class AbstractWorkflow implements Workflow {

    private final String name;

    private final Scheduler scheduler;

    private final Downloader downloader;

    private final List<ResponseHandler> responseHandlers;

    private final Consumer consumer;

    private final long sleepTime;

    private final int retryCount;

    private final AbstractFilterScheduler filterScheduler;

    public AbstractWorkflow(String name, ReptileConfig config) {
        this.name = name;
        this.scheduler = config.getScheduler();
        this.downloader = config.getDownloader();
        this.responseHandlers = config.getResponseHandlers();
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
    public List<ResponseHandler> getResponseHandlers() {
        return responseHandlers;
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

    protected ResponseHandler selectHandler(ResponseContext responseContext) {

        for (ResponseHandler responseHandler : getResponseHandlers()) {
            if (responseHandler.isSupport(responseContext)) {
                return responseHandler;
            }
        }
        return null;
    }
}
