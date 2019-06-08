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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public class DistributedWorkflow extends AbstractWorkflow {

    private final Logger LOGGER = LoggerFactory.getLogger(DistributedWorkflow.class);

    private final CountDownLatch latch;

    public DistributedWorkflow(CountDownLatch latch, String name, ReptileConfig config) {
        super(name, config);
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
                    LOGGER.warn("Failed to handle response, [{}], request: [{}], response: {}", throwable.getMessage(), request, response);
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
