package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.scheduler.Scheduler;
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

    public AbstractWorkflow(String name, Scheduler scheduler, Downloader downloader,
                            List<ResponseHandler> responseHandlers, Consumer consumer, long sleepTime) {
        this.name = name;
        this.scheduler = scheduler;
        this.downloader = downloader;
        this.responseHandlers = responseHandlers;
        this.consumer = consumer;
        this.sleepTime = sleepTime;
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

    protected ResponseHandler selectHandler(Request request, Response response) {

        for (ResponseHandler responseHandler : getResponseHandlers()) {
            if (responseHandler.isSupport(request, response)) {
                return responseHandler;
            }
        }
        return null;
    }
}
