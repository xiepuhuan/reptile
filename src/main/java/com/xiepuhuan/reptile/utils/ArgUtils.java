package com.xiepuhuan.reptile.utils;

import java.util.Collection;

/**
 * @author xiepuhuan
 */
public class ArgUtils {

    public static void notNull(final Object object, final String name) {
        if (object == null) {
            throw new NullPointerException(name + "can be not null");
        }
    }

    public static void notEmpty(final String str, final String name) {
        if (str == null) {
            throw new NullPointerException(name + "can be not null");
        }

        if (str.length() == 0) {
            throw new IllegalArgumentException(name + "can be not empty");
        }
    }

    public static boolean notEmpty(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }

        return true;
    }

    public static boolean notEmpty(final Object[] objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }

        return true;
    }

    public static void notEmpty(final Collection collection, final String name) {
        if (collection == null) {
            throw new NullPointerException(name + "can be not null");
        }

        if (collection.size() == 0) {
            throw new IllegalArgumentException(name + "can be not empty");
        }
    }

    public static void notEmpty(final Object[] objects, final String name) {
        if (objects == null) {
            throw new NullPointerException(name + "can be not null");
        }

        if (objects.length == 0) {
            throw new IllegalArgumentException(name + "can be not empty");
        }
    }


    public static void check(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void check(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
