package com.xiepuhuan.reptile.workflow;

import com.xiepuhuan.reptile.ReptileConfig;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.downloader.Downloader;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.handler.impl.NonResponseResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author xiepuhuan
 */
public class Workflow implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(Workflow.class);

    private static final ResponseHandler DEFAULT_RESPONSE_HANDLER = new NonResponseResponseHandler();

    private final String name;

    private final AtomicInteger activeThreadCount;

    private final Object requestArrived;

    private final CountDownLatch latch;

    private final Scheduler scheduler;

    private final Downloader downloader;

    private final List<ResponseHandler> responseHandlers;

    private final Consumer consumer;

    private final long sleepTime;

    public Workflow(CountDownLatch latch, AtomicInteger activeThreadCount, Object requestArrived,
                    String name, ReptileConfig config) {

        this.activeThreadCount = activeThreadCount;
        this.requestArrived = requestArrived;
        this.name = name;
        this.latch = latch;
        this.scheduler = config.getScheduler();
        this.downloader = config.getDownloader();
        this.responseHandlers = config.getResponseHandlers();
        this.consumer = config.getConsumer();
        this.sleepTime = config.getSleepTime();
    }

    @Override
    public void run() {
        LOGGER.info("Workflow [{}] start up", name);
        activeThreadCount.incrementAndGet();
        for (;!Thread.interrupted();) {

            Request request = null;
            try {
                request = scheduler.poll();
                if (request == null) {

                    if (activeThreadCount.decrementAndGet() <= 0) {
                        if (activeThreadCount.get() == 0) {
                            synchronized (requestArrived) {
                                requestArrived.notifyAll();
                            }
                        }
                        LOGGER.info("All requests have been handled and workflow [{}] exits", name);
                        latch.countDown();
                        return;
                    }
                    synchronized (requestArrived) {
                        requestArrived.wait();
                    }
                    if (activeThreadCount.get() != 0) {
                        activeThreadCount.incrementAndGet();
                    }
                    continue;
                }
                Response response = downloader.download(request);

                ResponseHandler requestResponseHandler = selectHandler(request, response);
                Result result = new Result();

                List<Request> requests = null;
                try {
                    requests = requestResponseHandler.handler(response, result);
                } catch (Exception e) {
                    LOGGER.warn("Failed to handle response: [{}]", e.getMessage());
                    continue;
                }

                int requestSize = requests == null ? 0 : requests.size();
                scheduler.push(requests);
                if (requestSize > 0) {
                    synchronized (requestArrived) {
                        requestArrived.notify();
                    }
                }
                consumer.consume(result);

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to download response about request [{}]: {}", request.toString(), e.getMessage());
            } catch (InterruptedException e) {
                break;
            }
        }
        latch.countDown();
        LOGGER.info("Thread [{}] are interrupted to exit workflow [{}]", Thread.currentThread().getName(), name);
    }

    private ResponseHandler selectHandler(Request request, Response response) {

        for (ResponseHandler responseHandler : responseHandlers) {
            if (responseHandler.isSupport(request, response)) {
                return responseHandler;
            }
        }

        LOGGER.warn("No response was found for the response handler to handle the request [{}], the nonResponseResponseHandler will be used", request.toString());

        return DEFAULT_RESPONSE_HANDLER;
    }
}
