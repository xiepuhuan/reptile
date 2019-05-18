package com.xiepuhuan.reptile.scheduler.filter;

/**
 * @author xiepuhuan
 */
public interface CloseableRequestFilter extends RequestFilter {

    void close();
}
