package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.Scheduler;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.BloomRequestFilter;
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

    protected abstract void push(Request request);

    @Override
    public void push(Request... requests) {
        if (ObjectUtils.isEmpty(requests)) {
            return;
        }

        for (Request request : requests) {
            if (!requestFilter.filter(request)) {
                push(request);
            }
        }
    }

    @Override
    public void push(Collection<Request> requests) {

        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        for (Request request : requests) {
            if (!requestFilter.filter(request)) {
                push(request);
            }
        }
    }
}
