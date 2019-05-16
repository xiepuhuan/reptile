package com.xiepuhuan.reptile.scheduler.filter.impl;

import com.google.common.hash.BloomFilter;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.RequestFilter;
import java.nio.charset.Charset;

/**
 * @author xiepuhuan
 */
public class BloomRequestFilter implements RequestFilter {

    public static final int DEFAULT_EXPECTED_INSERTIONS = 100000;

    public static final double DEFAULT_FPP = 0.03;

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private BloomFilter<Request> bloomFilter;

    public BloomRequestFilter(int expectedInsertions, double fpp) {

        bloomFilter = BloomFilter.create((from, into) -> {
            into.putString(from.getMethod(), DEFAULT_CHARSET)
                    .putString(from.getUrl(), DEFAULT_CHARSET);
        }, expectedInsertions, fpp);
    }

    public BloomRequestFilter(double fpp) {
        this(DEFAULT_EXPECTED_INSERTIONS, fpp);
    }

    public BloomRequestFilter(int expectedInsertions) {
        this(expectedInsertions, DEFAULT_FPP);
    }

    public BloomRequestFilter() {
        this(DEFAULT_EXPECTED_INSERTIONS, DEFAULT_FPP);
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
