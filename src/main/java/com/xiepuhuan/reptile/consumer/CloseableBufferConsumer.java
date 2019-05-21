package com.xiepuhuan.reptile.consumer;

/**
 * @author xiepuhuan
 */
public interface CloseableBufferConsumer extends CloseableConsumer {

    void flush();
}
