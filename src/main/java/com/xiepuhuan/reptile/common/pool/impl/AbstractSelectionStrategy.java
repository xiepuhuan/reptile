package com.xiepuhuan.reptile.common.pool.impl;

import com.xiepuhuan.reptile.common.pool.SelectionStrategy;

/**
 * @author xiepuhuan
 */
public abstract class AbstractSelectionStrategy implements SelectionStrategy {

    final int size;

    public AbstractSelectionStrategy(int size) {
        this.size = size;
    }
}
