package com.xiepuhuan.reptile.downloader.common;

import java.util.Collection;

/**
 * @author xiepuhuan
 */
public interface Pool<T> {

    T selectOne();

    int getSize();

    void add(T value);

    void addAll(T... values);

    void addAll(Collection<T> values);

    void remove(T value);

    void clear();
}
