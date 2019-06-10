package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.google.common.hash.BloomFilter;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.constants.BloomFilterConstants;
import java.nio.charset.Charset;

/**
 * @author xiepuhuan
 */
public class BloomRequestFilter implements RequestFilter {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private final BloomFilter<Request> bloomFilter;

    public BloomRequestFilter(int expectedInsertions, double fpp) {

        bloomFilter = BloomFilter.create((from, into) -> {
            into.putString(from.getMethod(), DEFAULT_CHARSET)
                    .putString(from.getUrl(), DEFAULT_CHARSET);
        }, expectedInsertions, fpp);
    }

    public BloomRequestFilter(double fpp) {
        this(BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS, fpp);
    }

    public BloomRequestFilter(int expectedInsertions) {
        this(expectedInsertions, BloomFilterConstants.DEFAULT_FPP);
    }

    public BloomRequestFilter() {
        this(BloomFilterConstants.DEFAULT_EXPECTED_INSERTIONS, BloomFilterConstants.DEFAULT_FPP);
    }

    @Override
    public boolean filter(Request request) {
        if (bloomFilter.mightContain(request)) {
            return true;
        }

        bloomFilter.put(request);
        return false;
    }
}
