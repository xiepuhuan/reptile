package com.xiepuhuan.reptile.workflow;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import java.util.List;

/**
 * @author xiepuhuan
 */
public interface Workflow extends Runnable {

    String getName();

    Scheduler getScheduler();

    Downloader getDownloader();

    List<ResponseHandler> getResponseHandlers();

    Consumer getConsumer();

    long getSleepTime();
}
