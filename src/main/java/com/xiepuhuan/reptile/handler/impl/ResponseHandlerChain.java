package com.xiepuhuan.reptile.handler.impl;

import com.google.common.collect.Lists;
import com.xiepuhuan.reptile.handler.CloseableResponseHandler;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.utils.ObjectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResponseHandlerChain implements CloseableResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandlerChain.class);

    private List<ResponseHandler> responseHandlers = Lists.newArrayList();

    public ResponseHandlerChain() {}

    public ResponseHandlerChain(List<ResponseHandler> responseHandlers) {
        if (CollectionUtils.isNotEmpty(responseHandlers)) {
            this.responseHandlers.addAll(responseHandlers);
        }
    }

    public static ResponseHandlerChain create() {
        return new ResponseHandlerChain();
    }

    public static ResponseHandlerChain create(List<ResponseHandler> responseHandlers) {
        return new ResponseHandlerChain(responseHandlers);
    }

    public ResponseHandlerChain addResponseHandler(ResponseHandler responseHandler) {
        Optional.ofNullable(responseHandler).ifPresent(responseHandlers::add);
        return this;
    }

    @Override
    public List<Request> handle(ResponseContext responseContext, Result result) throws Throwable {
        List<Request> requests = Lists.newArrayList();

        List<ResponseHandler> supportHandlerList = responseHandlers.stream()
                .filter(x -> x.isSupport(responseContext))
                .collect(Collectors.toList());

        if (supportHandlerList.isEmpty()) {
            LOGGER.warn("No response was found for the response handler to handle the request [{}]", responseContext.getRequest());
            return null;
        }

        for (ResponseHandler responseHandler : supportHandlerList) {
            try {
                List<Request> requestList = responseHandler.handle(responseContext, result);
                if(CollectionUtils.isNotEmpty(requestList)) {
                    requests.addAll(requestList);
                }
            } catch (Throwable e) {
                LOGGER.warn("Failed to handle response, [{}], request: [{}], response: {}",
                        e.getMessage(), responseContext.getRequest(), responseContext.getResponse());
            }
        }
        return requests;
    }

    @Override
    public boolean isSupport(ResponseContext responseContext) {
        return true;
    }

    @Override
    public void close() throws IOException {
        for (ResponseHandler responseHandler : responseHandlers) {
            try {
                if (responseHandler instanceof CloseableResponseHandler) {
                    ((CloseableResponseHandler) responseHandler).close();
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to close [{}]: {}", ObjectUtils.getSimpleName(responseHandler), e.getMessage());
            }
        }
    }
}
