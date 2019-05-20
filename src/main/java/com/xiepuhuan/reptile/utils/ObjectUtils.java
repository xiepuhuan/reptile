package com.xiepuhuan.reptile.utils;

/**
 * @author xiepuhuan
 */
public class ObjectUtils {

    private ObjectUtils() {}

    public static String getSimpleName(Object o) {
        return o.getClass().getSimpleName();
    }
}
