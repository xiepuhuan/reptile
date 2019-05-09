package com.xiepuhuan.reptile.scheduler.filter;

import com.xiepuhuan.reptile.model.Request;

/**
 * @author xiepuhuan
 */
public interface RequestFilter {

    boolean filter(Request request);
}
