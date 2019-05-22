package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public class DistributedWorkflow extends AbstractWorkflow {

    private final Logger LOGGER = LoggerFactory.getLogger(DistributedWorkflow.class);

    private final CountDownLatch latch;

    public DistributedWorkflow(CountDownLatch latch, String name, ReptileConfig config) {

        super(name, config.getScheduler(), config.getDownloader(), config.getResponseHandlers(), config.getConsumer(), config.getSleepTime());
        this.latch = latch;
    }

    @Override
    public void run() {
        LOGGER.info("DistributedWorkflow [{}] start up", getName());
        for (; !Thread.interrupted(); ) {
            try {
                Request request = getScheduler().take();

                Response response = null;

                try {
                    response = getDownloader().download(request);
                } catch (IOException | IllegalStateException e) {
                    LOGGER.warn("Failed to download response about request [{}]: {}", request.toString(), e.getMessage());
                    continue;
                }

                ResponseContext responseContext = new ResponseContext(request, response);
                ResponseHandler requestResponseHandler = selectHandler(responseContext);

                if (requestResponseHandler == null) {
                    LOGGER.warn("No response was found for the response handler to handle the request [{}], the nonResponseResponseHandler will be used", request.toString());
                    continue;
                }

                Result result = new Result();
                List<Request> requests = null;
                try {
                    requests = requestResponseHandler.handle(responseContext, result);
                } catch (Throwable throwable) {
                    LOGGER.warn("Failed to handle response: [{}]", throwable.getMessage());
                    continue;
                }
                getScheduler().put(requests);

                try {
                    getConsumer().consume(result);
                } catch (Throwable throwable) {
                    LOGGER.warn("Failed to comsume result: [{}]", throwable.getMessage());
                }

                if (getSleepTime() > 0) {
                    Thread.sleep(getSleepTime());
                }

                if (getSleepTime() > 0) {
                    Thread.sleep(getSleepTime());
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        latch.countDown();
        LOGGER.info("Thread [{}] are interrupted to exit workflow [{}]", Thread.currentThread().getName(), getName());
    }
}
