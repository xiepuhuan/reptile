package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.constants.RequestAttributesConstants;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.xiepuhuan.reptile.workflow.context.WorkflowContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单机工作流
 * @author xiepuhuan
 */
public class SingleWorkflow extends AbstractWorkflow {

    private final Logger LOGGER = LoggerFactory.getLogger(SingleWorkflow.class);

    private final AtomicInteger activeThreadCount;

    private final Object requestArrived;

    private final CountDownLatch activeThreadLatch;

    public SingleWorkflow(CountDownLatch activeThreadLatch, AtomicInteger activeThreadCount, Object requestArrived,
                          String name, ReptileConfig config) {

        super(name, config);
        this.activeThreadCount = activeThreadCount;
        this.requestArrived = requestArrived;
        this.activeThreadLatch = activeThreadLatch;
    }

    @Override
    public void run() {
        LOGGER.info("SingleWorkflow [{}] start up", getName());
        activeThreadCount.incrementAndGet();
        for (; !Thread.interrupted(); ) {

            Request request = null;
            try {
                request = getScheduler().poll();
                if (request == null) {

                    if (activeThreadCount.decrementAndGet() == 0) {
                        synchronized (requestArrived) {
                            requestArrived.notify();
                        }
                        LOGGER.info("All requests have been handled and workflow [{}] exits", getName());
                        activeThreadLatch.countDown();
                        return;
                    }
                    synchronized (requestArrived) {
                        requestArrived.wait();
                    }

                    activeThreadCount.incrementAndGet();
                    continue;
                }
                Response response = null;
                try {
                    response = getDownloader().download(request);
                } catch (IOException | IllegalStateException e) {
                    LOGGER.warn("Failed to download response about request [{}]: {}", request, e.getMessage());
                    if (getRetryCount() > 0 && getFilterScheduler() != null) {
                        Object retryCount = request.getAttribute(RequestAttributesConstants.REQUEST_RETRY_COUNT);
                        int rc = 1;
                        if (retryCount instanceof Integer) {
                            if ((rc = (Integer) retryCount + 1) > getRetryCount()) {
                                LOGGER.info("The number of retries requested has reached the maximum of {}: {}", getRetryCount(), request);
                                continue;
                            }
                        }
                        request.setAttribute(RequestAttributesConstants.REQUEST_RETRY_COUNT, rc);
                        LOGGER.info("Request [{}] reentry to scheduler", request.toString());
                        getFilterScheduler().pushUnfiltered(request);
                    }
                    continue;
                }
                ResponseContext responseContext = new ResponseContext(request, response);
                Result result = new Result();

                List<Request> requests = null;
                try {
                    requests = getResponseHandlerChain().handle(responseContext, result);
                } catch (Throwable throwable) {
                    LOGGER.warn("Failed to handle response, [{}], request: [{}], response: {}", throwable.getMessage(), request, response);
                    continue;
                }

                int requestSize = requests == null ? 0 : requests.size();
                getScheduler().push(requests);

                if (requestSize > 0) {
                    synchronized (requestArrived) {
                        requestArrived.notifyAll();
                    }
                }
                try {
                    if (!result.isIgnore()) {
                        getConsumer().consume(result);
                    }
                } catch (Throwable throwable) {
                    LOGGER.warn("Failed to comsume result: [{}]", throwable.getMessage());
                }

                if (getSleepTime() > 0) {
                    Thread.sleep(getSleepTime());
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        activeThreadLatch.countDown();
        LOGGER.info("Thread [{}] are interrupted to exit workflow [{}]", Thread.currentThread().getName(), getName());
    }
}
