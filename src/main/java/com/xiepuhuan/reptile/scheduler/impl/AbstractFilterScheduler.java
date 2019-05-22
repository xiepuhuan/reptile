package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.BloomRequestFilter;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author xiepuhuan
 */
public abstract class AbstractFilterScheduler implements Scheduler {

    private RequestFilter requestFilter;

    public AbstractFilterScheduler(RequestFilter requestFilter) {

        if (requestFilter == null) {
            requestFilter = new BloomRequestFilter();
        }

        this.requestFilter = requestFilter;
    }

    public boolean isNotFilter(Request request) {
        return !requestFilter.filter(request);
    }

    public abstract void pushUnfiltered(Request request);

    public abstract void putUnfiltered(Request request) throws InterruptedException;

    @Override
    public void push(Request request) {
        if (isNotFilter(request)) {
            pushUnfiltered(request);
        }
    }

    @Override
    public void push(Request... requests) {
        if (ObjectUtils.isEmpty(requests)) {
            return;
        }

        Arrays.stream(requests).forEach(this::push);
    }

    @Override
    public void push(Collection<Request> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        requests.forEach(this::push);
    }


    @Override
    public void put(Request request) throws InterruptedException {
        if (isNotFilter(request)) {
            putUnfiltered(request);
        }
    }

    @Override
    public void put(Request... requests) throws InterruptedException {
        if (ObjectUtils.isEmpty(requests)) {
            return;
        }

        for (Request request : requests) {
            put(request);
        }
    }

    @Override
    public void put(Collection<Request> requests) throws InterruptedException {
        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        for (Request request : requests) {
            put(request);
        }
    }

    public RequestFilter getRequestFilter() {
        return requestFilter;
    }
}
