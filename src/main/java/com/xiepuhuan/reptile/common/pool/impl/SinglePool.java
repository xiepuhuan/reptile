package com.xiepuhuan.reptile.common.pool.impl;

/**
 * @author xiepuhuan
 */
public class SinglePool<T> extends AbstractFixedPool<T> {

    private final T object;

    public SinglePool(T object) {
        super(1);
        this.object = object;
    }

    @Override
    public T selectOne() {
        return object;
    }
}
