package com.xiepuhuan.reptile.handler;

import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import java.util.List;

public interface ResponseHandler {

     List<Request> handle(Response response, Result result);

     boolean isSupport(Request request, Response response);
}
