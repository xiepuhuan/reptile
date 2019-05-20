package com.xiepuhuan.reptile.scheduler;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author xiepuhuan
 */
public interface CloseableScheduler extends Scheduler, Closeable {

    void close() throws IOException;
}
