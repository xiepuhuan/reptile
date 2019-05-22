package com.xiepuhuan.reptile.model;

/**
 * @author xiepuhuan
 */
public class ResponseContext {

    private Request request;

    private Response response;

    public ResponseContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
