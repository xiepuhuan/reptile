package com.xiepuhuan.reptile.scheduler.impl;

import com.xiepuhuan.reptile.common.redis.RedisClientManager;
import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.CloseableScheduler;
import com.xiepuhuan.reptile.scheduler.filter.CloseableRequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;

/**
 * @author xiepuhuan
 */
public class RedisFIFOQueueScheduler extends AbstractFilterScheduler implements CloseableScheduler {

    public static final String DEFAULT_REQUEST_QUEUE_NAME = "reptile_request_queue";

    private final RBlockingQueue<Request> queue;

    private final RedissonClient redissonClient;

    private final RedisConfig redisConfig;

    private final String name;

    private RedisFIFOQueueScheduler(RedisConfig redisConfig, String name, RequestFilter requestFilter, boolean recreate) {
        super(requestFilter);
        ArgUtils.notNull(redisConfig, "redisConfig");
        ArgUtils.notEmpty(name, "queueName");
        redisConfig.check();
        this.redisConfig = redisConfig;
        this.name = name;
        this.redissonClient = RedisClientManager.createRedissonClient(redisConfig);
        this.queue = redissonClient.getBlockingQueue(name);
        if (recreate && queue.isExists()) {
            queue.delete();
        }
    }

    public static class Builder {

        private RedisConfig redisConfig;

        private String name;

        private boolean recreate;

        private RequestFilter requestFilter;

        public Builder() {
            this.redisConfig = RedisConfig.DEFAULT_REDIS_CONFIG;
            this.name = DEFAULT_REQUEST_QUEUE_NAME;
            this.recreate = false;
            this.requestFilter = null;
        }

        public Builder setRedisConfig(RedisConfig redisConfig) {
            this.redisConfig = redisConfig;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setRecreate(boolean recreate) {
            this.recreate = recreate;
            return this;
        }

        public Builder setRequestFilter(RequestFilter requestFilter) {
            this.requestFilter = requestFilter;
            return this;
        }

        public static Builder custom() {
            return new Builder();
        }

        public RedisFIFOQueueScheduler build() {
            return new RedisFIFOQueueScheduler(redisConfig, name, requestFilter, recreate);
        }
    }

    @Override
    public void pushUnfiltered(Request request) {
        queue.offer(request);
    }

    @Override
    public Request poll() {
        return queue.poll();
    }

    @Override
    public void putUnfiltered(Request request) throws InterruptedException {
        queue.put(request);
    }

    @Override
    public Request take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public synchronized void close() {
        redissonClient.shutdown();

        if (getRequestFilter() instanceof CloseableRequestFilter) {
            ((CloseableRequestFilter) getRequestFilter()).close();
        }
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public String getName() {
        return name;
    }
}
