package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.downloader.constants.SelectionStrategyEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author xiepuhuan
 */
@Getter
@Builder
@NoArgsConstructor
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
}
