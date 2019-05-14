package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.downloader.constants.SelectionStrategyEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author xiepuhuan
 */
public class PoolConfig<T> {

    private Collection<T> pool;

    private SelectionStrategyEnum selectionStrategy;

    private PoolConfig(Collection<T> pool, SelectionStrategyEnum selectionStrategy) {
        this.pool = pool;
        this.selectionStrategy = selectionStrategy;
    }

    public Collection<T> getPool() {
        return pool;
    }

    public SelectionStrategyEnum getSelectionStrategy() {
        return selectionStrategy;
    }

    public static class Builder<T> {
        private Collection<T> pool;

        private SelectionStrategyEnum selectionStrategy;

        private Builder() {
            this.pool = new ArrayList<>();
            this.selectionStrategy = SelectionStrategyEnum.ORDER;
        }

        public Builder<T> appendAll(Collection<T> collection) {
            this.pool.addAll(collection);
            return this;
        }

        public Builder<T> appendAll(T... values) {
            this.pool.addAll(Arrays.asList(values));
            return this;
        }

        public Builder<T> setSelectionStrategy(SelectionStrategyEnum selectionStrategy) {
            this.selectionStrategy = selectionStrategy;
            return this;
        }

        public <T> PoolConfig<T> build() {
            return new PoolConfig(pool, selectionStrategy);
        }

        public static <T> Builder<T> custom() {
            return new Builder<>();
        }

        public static <T> PoolConfig<T> create() {
            return (PoolConfig<T>) new Builder<>().build();
        }
    }
}
