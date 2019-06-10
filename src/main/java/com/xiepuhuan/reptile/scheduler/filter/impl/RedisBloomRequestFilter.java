package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.common.redis.RedisClientManager;
import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.CloseableRequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.constants.BloomFilterConstants;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

/**
 * @author xiepuhuan
 */
public class RedisBloomRequestFilter implements CloseableRequestFilter {

    public static final String DEFAULT_BLOOM_FILTER_NAME = "reptile_request_bloom_filter";

    private final RedissonClient redissonClient;

    private final RedisConfig redisConfig;

    private final String name;

    private final int expectedInsertions;

    private final double fpp;

    private final RBloomFilter<Request> bloomFilter;

    private RedisBloomRequestFilter(RedisConfig redisConfig, String name, int expectedInsertions, double fpp, boolean recreate) {
        ArgUtils.notNull(recreate, "redisConfig");
        redisConfig.check();
        ArgUtils.notEmpty(name, "bloomFilter name");
        ArgUtils.check(expectedInsertions > 0, "expectedInsertions must be more than 0");
        ArgUtils.check(fpp > 0.0 && fpp < 1.0, "fpp must be positive and less than 1.0");

        this.redissonClient = RedisClientManager.createRedissonClient(redisConfig);
        this.redisConfig = redisConfig;
        this.name = name;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = redissonClient.getBloomFilter(name);

        if (recreate && bloomFilter.isExists()) {
            bloomFilter.delete();
        }

        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, fpp);
        }
    }

    public static class Builder {

        private RedisConfig redisConfig;

        private String name;

        private int expectedInsertions;

        private double fpp;

        private boolean recreate;

        public Builder() {
            this.redisConfig = RedisConfig.DEFAULT_REDIS_CONFIG;
            this.name = DEFAULT_BLOOM_FILTER_NAME;
            this.expectedInsertions = BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS;
            this.fpp = BloomFilterConstants.DEFAULT_FPP;
            this.recreate = false;
        }

        public Builder setRedisConfig(RedisConfig redisConfig) {
            this.redisConfig = redisConfig;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setExpectedInsertions(int expectedInsertions) {
            this.expectedInsertions = expectedInsertions;
            return this;
        }

        public Builder setFpp(double fpp) {
            this.fpp = fpp;
            return this;
        }

        public Builder setRecreate(boolean recreate) {
            this.recreate = recreate;
            return this;
        }

        public static Builder custom() {
            return new Builder();
        }

        public RedisBloomRequestFilter build() {
            return new RedisBloomRequestFilter(redisConfig, name, expectedInsertions, fpp, recreate);
        }

        public static RedisBloomRequestFilter create() {
            return custom().build();
        }
    }

    @Override
    public boolean filter(Request request) {
        return !bloomFilter.add(request);
    }

    @Override
    public synchronized void close() {
        redissonClient.shutdown();
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
