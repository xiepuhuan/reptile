package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.common.redis.RedisClientManager;
import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.CloseableRequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.constants.BloomFilterConstants;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBloomFilter;

/**
 * @author xiepuhuan
 */
public class RedisBloomRequestFilter implements CloseableRequestFilter {

    public static final String DEFAULT_BLOOM_FILTER_NAME = "reptile_request_bloom_filter";

    private RBloomFilter<Request> bloomFilter;

    private final RedisConfig redisConfig;

    private final String name;

    private final int expectedInsertions;

    private final double fpp;

    public RedisBloomRequestFilter(RedisConfig redisConfig, String name, int expectedInsertions, double fpp, boolean recreate) {
        ArgUtils.notNull(recreate, "redisConfig");
        redisConfig.check();
        ArgUtils.notEmpty(name, "bloomFilter name");
        ArgUtils.check(expectedInsertions > 0, "expectedInsertions must be more than 0");
        ArgUtils.check(fpp > 0.0 && fpp < 1.0, "fpp must be positive and less than 1.0");

        this.redisConfig = redisConfig;
        this.name = name;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        bloomFilter = RedisClientManager.getRedissonClient(redisConfig).getBloomFilter(name);

        if (recreate && bloomFilter.isExists()) {
            bloomFilter.delete();
        }

        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, fpp);
        }
    }

    public RedisBloomRequestFilter(String name, boolean recreate) {
        this(RedisConfig.DEFAULT_REDIS_CONFIG, name, BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS, BloomFilterConstants.DEFAULT_FPP, recreate);
    }

    public RedisBloomRequestFilter(RedisConfig redisConfig) {
        this(redisConfig, DEFAULT_BLOOM_FILTER_NAME, BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS, BloomFilterConstants.DEFAULT_FPP, false);
    }

    public RedisBloomRequestFilter() {
        this(RedisConfig.DEFAULT_REDIS_CONFIG, DEFAULT_BLOOM_FILTER_NAME, BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS, BloomFilterConstants.DEFAULT_FPP, false);
    }

    @Override
    public boolean filter(Request request) {
        return !bloomFilter.add(request);
    }

    @Override
    public void close() {
        RedisClientManager.shutdownRedisClient(redisConfig);
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public String getName() {
        return name;
    }

    public int getExpectedInsertions() {
        return expectedInsertions;
    }

    public double getFpp() {
        return fpp;
    }
}
