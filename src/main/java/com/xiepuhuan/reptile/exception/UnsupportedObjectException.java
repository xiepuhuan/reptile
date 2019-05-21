package com.xiepuhuan.reptile.exception;

/**
 * @author xiepuhuan
 */
public class UnsupportedObjectException extends RuntimeException {
    public UnsupportedObjectException() {
    }

    public UnsupportedObjectException(String message) {
        super(message);
    }

    public UnsupportedObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedObjectException(Throwable cause) {
        super(cause);
    }
}
