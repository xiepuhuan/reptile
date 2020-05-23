package com.xiepuhuan.reptile.workflow;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.impl.ResponseHandlerChain;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.impl.AbstractFilterScheduler;

/**
 * @author xiepuhuan
 */
public interface Workflow extends Runnable {

    String getName();

    Scheduler getScheduler();

    AbstractFilterScheduler getFilterScheduler();

    Downloader getDownloader();

    ResponseHandlerChain getResponseHandlerChain();

    Consumer getConsumer();

    long getSleepTime();

    int getRetryCount();
}
