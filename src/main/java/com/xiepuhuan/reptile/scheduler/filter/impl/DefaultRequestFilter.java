package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;

/**
 * @author xiepuhuan
 */
public class DefaultRequestFilter implements RequestFilter {

    @Override
    public boolean filter(Request request) {
        return false;
    }
}
