package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiepuhuan
 */
public class HashSetRequestFilter implements RequestFilter {

    private Set<Request> requestSet;

    public HashSetRequestFilter() {
        this.requestSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public boolean filter(Request request) {
        return !requestSet.add(request);
    }
}
