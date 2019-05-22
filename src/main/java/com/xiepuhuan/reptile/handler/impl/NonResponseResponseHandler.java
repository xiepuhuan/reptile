package com.xiepuhuan.reptile.handler.impl;

import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public class NonResponseResponseHandler implements ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonResponseResponseHandler.class);

    private BlockingQueue<Response> unhandledQueue;

    public NonResponseResponseHandler() {
        this.unhandledQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public List<Request> handle(ResponseContext responseContext, Result result) {

        unhandledQueue.offer(responseContext.getResponse());
        return Collections.emptyList();
    }

    @Override
    public boolean isSupport(ResponseContext responseContext) {
        return true;
    }
}
