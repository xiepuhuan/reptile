package com.xiepuhuan.reptile.downloader.common.impl;

import com.xiepuhuan.reptile.downloader.common.SelectionStrategy;

/**
 * @author xiepuhuan
 */
public abstract class AbstractSelectionStrategy implements SelectionStrategy {

    final int size;

    public AbstractSelectionStrategy(int size) {
        this.size = size;
    }
}
