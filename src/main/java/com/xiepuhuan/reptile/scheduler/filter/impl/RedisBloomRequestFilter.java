package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.common.redis.RedisClientManager;
import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.CloseableRequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBloomFilter;

/**
 * @author xiepuhuan
 */
public class RedisBloomRequestFilter implements CloseableRequestFilter {

    public static final String DEFAULT_BLOOM_FILTER_NAME = "reptile_request_bloom_filter";

    private RBloomFilter<Request> bloomFilter;

    private final RedisConfig redisConfig;

    public RedisBloomRequestFilter(String name, RedisConfig redisConfig) {
        ArgUtils.notEmpty(name, "bloomFilter name");

        bloomFilter = RedisClientManager.getRedissonClient(redisConfig).getBloomFilter(name);
        this.redisConfig = redisConfig;
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(BloomRequestFilter.DEFAULT_EXPECTED_INSERTIONS, BloomRequestFilter.DEFAULT_FPP);
        }
    }

    public RedisBloomRequestFilter(RedisConfig redisConfig) {
        this(DEFAULT_BLOOM_FILTER_NAME, redisConfig);
    }

    public RedisBloomRequestFilter() {
        this(DEFAULT_BLOOM_FILTER_NAME, RedisConfig.DEFAULT_REDIS_CONFIG);
    }

    @Override
    public boolean filter(Request request) {
        return !bloomFilter.add(request);
    }

    @Override
    public void close() {
        RedisClientManager.shutdownRedisClient(redisConfig);
    }
}
