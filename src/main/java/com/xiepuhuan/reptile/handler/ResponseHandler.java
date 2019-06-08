package com.xiepuhuan.reptile.handler;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import java.util.List;

public interface ResponseHandler {

     List<Request> handle(ResponseContext responseContext, Result result) throws Throwable;

     boolean isSupport(ResponseContext responseContext);
}
