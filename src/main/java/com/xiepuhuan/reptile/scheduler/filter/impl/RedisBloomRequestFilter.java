package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.xiepuhuan.reptile.common.redis.RedissonClientHolder;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.api.RBloomFilter;

/**
 * @author xiepuhuan
 */
public class RedisBloomRequestFilter implements RequestFilter {

    public static final String DEFAULT_BLOOM_FILTER_NAME = "reptile_request_bloom_filter";

    private RBloomFilter<Request> bloomFilter;

    public RedisBloomRequestFilter(String name) {
        ArgUtils.notEmpty(name, "bloomFilter name");
        bloomFilter = RedissonClientHolder.getRedissonClient().getBloomFilter(name);
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(BloomRequestFilter.DEFAULT_EXPECTED_INSERTIONS, BloomRequestFilter.DEFAULT_FPP);
        }
    }

    public RedisBloomRequestFilter() {
        this(DEFAULT_BLOOM_FILTER_NAME);
    }

    @Override
    public boolean filter(Request request) {
        return !bloomFilter.add(request);
    }
}
