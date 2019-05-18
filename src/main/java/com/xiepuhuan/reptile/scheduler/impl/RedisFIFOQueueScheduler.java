package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.common.redis.RedisClientManager;
import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.CloseableScheduler;
import com.xiepuhuan.reptile.scheduler.filter.CloseableRequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.RedisBloomRequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBlockingQueue;

/**
 * @author xiepuhuan
 */
public class RedisFIFOQueueScheduler extends AbstractFilterScheduler implements CloseableScheduler {

    public static final String DEFAULT_REQUEST_QUEUE_NAME = "reptile_request_queue";

    private final RBlockingQueue<Request> queue;

    private final RedisConfig redisConfig;

    public RedisFIFOQueueScheduler(String queueName, RequestFilter requestFilter, RedisConfig redisConfig) {
        super(requestFilter);
        ArgUtils.notNull(queueName, "queueName");
        this.queue = RedisClientManager.getRedissonClient(redisConfig).getBlockingQueue(queueName);
        this.redisConfig = redisConfig;
    }

    public RedisFIFOQueueScheduler(String queueName) {
        this(queueName, new RedisBloomRequestFilter(), RedisConfig.DEFAULT_REDIS_CONFIG);
    }

    public RedisFIFOQueueScheduler() {
        this(DEFAULT_REQUEST_QUEUE_NAME, new RedisBloomRequestFilter(), RedisConfig.DEFAULT_REDIS_CONFIG);
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

    public void close() {
        RedisClientManager.shutdownRedisClient(redisConfig);

        if (getRequestFilter() instanceof CloseableRequestFilter) {
            ((CloseableRequestFilter) getRequestFilter()).close();
        }
    }
}
