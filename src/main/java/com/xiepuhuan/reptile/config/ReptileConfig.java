package com.xiepuhuan.reptile.config;

import com.google.common.collect.Lists;
import com.xiepuhuan.reptile.constants.DeploymentModeEnum;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.consumer.impl.ConsoleConsumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.downloader.impl.HttpClientDownloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.handler.impl.ResponseHandlerChain;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.impl.FIFOQueueScheduler;
import com.xiepuhuan.reptile.utils.ArgUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Reptile爬虫配置类
 * @author xiepuhuan
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReptileConfig {

    private String name = "reptile";

    private long sleepTime = 0;

    private boolean asynRun = false;

    private int threadCount = Runtime.getRuntime().availableProcessors();

    private int retryCount = 3;

    private DeploymentModeEnum deploymentMode = DeploymentModeEnum.SINGLE;

    private Scheduler scheduler = new FIFOQueueScheduler();

    private Downloader downloader = new HttpClientDownloader();

    private ResponseHandlerChain responseHandlerChain = new ResponseHandlerChain();

    private Consumer consumer = new ConsoleConsumer();

    public void check() {
        ArgUtils.notEmpty(name, "name");
        ArgUtils.check(sleepTime >= 0, "sleepTime must be greater than or equal to 0");
        ArgUtils.check(threadCount > 0, "threadCount must be greater than 0");
        ArgUtils.check(retryCount >= 0, "retryCount must be greater than or equal to 0");
        ArgUtils.notNull(deploymentMode, "deploymentMode");
        ArgUtils.notNull(scheduler, "scheduler");
        ArgUtils.notNull(downloader, "downloader");
        ArgUtils.notNull(responseHandlerChain, "responseHandlerChain");
        ArgUtils.notNull(consumer, "consumer");
    }
}

