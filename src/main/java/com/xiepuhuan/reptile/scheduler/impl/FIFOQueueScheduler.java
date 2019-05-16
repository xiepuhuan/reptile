package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.BloomRequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public class FIFOQueueScheduler extends AbstractFilterScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FIFOQueueScheduler.class);

    private ConcurrentLinkedQueue<Request> queue;

    public FIFOQueueScheduler(ConcurrentLinkedQueue<Request> queue, RequestFilter requestFilter) {
        super(requestFilter);
        ArgUtils.notNull(queue, "concurrentLinkedQueue");
        this.queue = queue;
    }

    public FIFOQueueScheduler(ConcurrentLinkedQueue<Request> queue) {
        this(queue, new BloomRequestFilter());
    }

    public FIFOQueueScheduler(RequestFilter requestFilter) {
        this(new ConcurrentLinkedQueue<>(), requestFilter);
    }

    public FIFOQueueScheduler() {
        this(new BloomRequestFilter());
    }

    @Override
    public void push(Request request) {
        queue.offer(request);
    }

    @Override
    public Request poll() {
        return queue.poll();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
