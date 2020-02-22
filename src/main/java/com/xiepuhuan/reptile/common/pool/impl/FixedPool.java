package com.xiepuhuan.reptile.common.pool.impl;

import com.xiepuhuan.reptile.common.pool.SelectionStrategy;
import com.xiepuhuan.reptile.config.PoolConfig;
import com.xiepuhuan.reptile.downloader.constants.SelectionStrategyEnum;
import com.xiepuhuan.reptile.utils.ArgUtils;
import com.xiepuhuan.reptile.utils.CollectionUtils;
import java.util.Collection;

/**
 * @author xiepuhuan
 */
public class FixedPool<T> extends AbstractFixedPool<T> {

    private final T[] pool;

    private final SelectionStrategy selectionStrategy;

    public FixedPool(Collection<T> pool, SelectionStrategyEnum selectionStrategy) {
        super(pool == null ? 0 : pool.size());
        ArgUtils.notEmpty(pool, "pool");
        this.pool = CollectionUtils.toArray(pool);
        this.selectionStrategy = buildSelectionStrategy(selectionStrategy);
    }

    public FixedPool(PoolConfig<T> config) {
        this(config.getPool(), config.getSelectionStrategy());
    }


    public FixedPool(T[] pool, SelectionStrategyEnum selectionStrategy) {
        super(pool == null ? 0 : pool.length);
        ArgUtils.notEmpty(pool, "pool");
        this.pool = pool;
        this.selectionStrategy = buildSelectionStrategy(selectionStrategy);
    }

    public FixedPool(Collection<T> pool) {
        this(pool, SelectionStrategyEnum.ORDER);
    }

    public FixedPool(T[] pool) {
        this(pool, SelectionStrategyEnum.ORDER);
    }

    private SelectionStrategy buildSelectionStrategy(SelectionStrategyEnum selectionStrategyEnum) {
        return selectionStrategyEnum == SelectionStrategyEnum.ORDER ?
                new OrderSelectionStrategy(getSize()) : new RandomSelectionStrategy(getSize());
    }

    @Override
    public T selectOne() {
        return pool[selectionStrategy.getIndex()];
    }
}
