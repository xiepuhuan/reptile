package com.xiepuhuan.reptile.downloader.common.impl;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author xiepuhuan
 */
public class RandomSelectionStrategy extends AbstractSelectionStrategy  {

    public RandomSelectionStrategy(int size) {
        super(size);
    }

    @Override
    public int getIndex() {
        return RandomUtils.nextInt() % size;
    }
}
