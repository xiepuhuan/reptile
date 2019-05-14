package com.xiepuhuan.reptile.downloader.common.impl;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiepuhuan
 */
public class OrderSelectionStrategy extends AbstractSelectionStrategy {

    private final AtomicInteger number = new AtomicInteger(0);

    public OrderSelectionStrategy(int size) {
        super(size);
    }

    @Override
    public int getIndex() {
        return number.getAndIncrement() % size;
    }
}
