package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.common.redis.RedissonClientHolder;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.RedisBloomRequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBlockingQueue;

/**
 * @author xiepuhuan
 */
public class RedisFIFOQueueScheduler extends AbstractFilterScheduler {

    public static final String DEFAULT_REQUEST_QUEUE_NAME = "reptile_request_queue";

    private final RBlockingQueue<Request> queue;

    public RedisFIFOQueueScheduler(String queueName, RequestFilter requestFilter) {
        super(requestFilter);
        ArgUtils.notNull(queueName, "queueName");
        this.queue = RedissonClientHolder.getRedissonClient().getBlockingQueue(queueName);
    }

    public RedisFIFOQueueScheduler(String queueName) {
        this(queueName, new RedisBloomRequestFilter());
    }

    public RedisFIFOQueueScheduler() {
        this(DEFAULT_REQUEST_QUEUE_NAME, new RedisBloomRequestFilter());
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
    public void put(Request requests) throws InterruptedException {
        queue.put(requests);
    }

    @Override
    public Request take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public int size() {
        return queue.size();
    }


}
