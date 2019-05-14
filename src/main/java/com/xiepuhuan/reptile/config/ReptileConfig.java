package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.consumer.impl.ConsoleConsumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.downloader.impl.HttpClientDownloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.impl.FIFOQueueScheduler;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiepuhuan
 */
public class ReptileConfig {

    private String name;

    private long sleepTime;

    private boolean asynRun;

    private int threadCount;

    private Scheduler scheduler;

    private Downloader downloader;

    private List<ResponseHandler> responseHandlers;

    private Consumer consumer;

    private ReptileConfig(String name, long sleepTime, boolean asynRun, int threadCount, Scheduler scheduler,
                         Downloader downloader, List<ResponseHandler> responseHandlers, Consumer consumer) {
        this.name = name;
        this.sleepTime = sleepTime;
        this.asynRun = asynRun;
        this.threadCount = threadCount;
        this.scheduler = scheduler;
        this.downloader = downloader;
        this.responseHandlers = responseHandlers;
        this.consumer = consumer;
    }

    public void check() {
        ArgUtils.notEmpty(name, "name");
        ArgUtils.check(sleepTime >= 0, "sleepTime must be greater than or equal to 0");
        ArgUtils.check(threadCount > 0, "threadCount must be greater than 0");
        ArgUtils.notNull(scheduler, "scheduler");
        ArgUtils.notNull(downloader, "downloader");
        ArgUtils.notNull(responseHandlers, "responseHandlers");
        ArgUtils.notNull(consumer, "consumer");
    }


    public String getName() {
        return name;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public boolean isAsynRun() {
        return asynRun;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public List<ResponseHandler> getResponseHandlers() {
        return responseHandlers;
    }

    public Consumer getConsumer() {
        return consumer;
    }


    public static class Builder {

        private String name;

        private long sleepTime;

        private boolean asynRun;

        private int threadCount;

        private Scheduler scheduler;

        private Downloader downloader;

        private List<ResponseHandler> responseHandlers;

        private Consumer consumer;

        private Builder() {
            this.name = "reptile";
            this.sleepTime = 0;
            this.asynRun = false;
            this.threadCount = Runtime.getRuntime().availableProcessors();
            this.scheduler = new FIFOQueueScheduler();
            this.downloader = new HttpClientDownloader();
            this.responseHandlers = new ArrayList<>();
            this.consumer = new ConsoleConsumer();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSleepTime(long sleepTime) {
            this.sleepTime = sleepTime;
            return this;
        }

        public Builder setAsynRun(boolean asynRun) {
            this.asynRun = asynRun;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder setDownloader(Downloader downloader) {
            this.downloader = downloader;
            return this;
        }

        public Builder appendResponseHandlers(List<ResponseHandler> responseHandlers) {
            this.responseHandlers.addAll(responseHandlers);
            return this;
        }

        public Builder appendResponseHandlers(ResponseHandler... responseHandlers) {
            this.responseHandlers.addAll(Arrays.asList(responseHandlers));
            return this;
        }

        public Builder setConsumer(Consumer consumer) {
            this.consumer = consumer;
            return this;
        }

        public static Builder cutom() {
            return new Builder();
        }

        public static ReptileConfig create() {
            return cutom().build();
        }

        public ReptileConfig build() {
            return new ReptileConfig(name, sleepTime, asynRun, threadCount, scheduler, downloader, responseHandlers, consumer);
        }
    }

}
