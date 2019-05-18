package com.xiepuhuan.reptile.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author xiepuhuan
 */
public class CollectionUtils {

    private CollectionUtils() {}

    public static <T> T[] toArray(Collection<T> collection) {
        ArgUtils.notNull(collection, "collection");

        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Object[collection.size()];
        Iterator<T> iterator = collection.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            array[i++] = iterator.next();
        }
        return array;
    }
}
