package com.xiepuhuan.reptile.common.pool.impl;

import com.xiepuhuan.reptile.common.pool.Pool;
import java.util.Collection;

/**
 * @author xiepuhuan
 */
public abstract class AbstractFixedPool<T> implements Pool<T> {

    private final int size;

    public AbstractFixedPool(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void add(T value) {
        throw new UnsupportedOperationException("add operations are not supported");
    }

    @Override
    public void addAll(T... values) {
        throw new UnsupportedOperationException("addAll operations are not supported");
    }

    @Override
    public void addAll(Collection<T> values) {
        throw new UnsupportedOperationException("addAll operations are not supported");
    }

    @Override
    public void remove(T value) {
        throw new UnsupportedOperationException("remove operations are not supported");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear operations are not supported");
    }
}
