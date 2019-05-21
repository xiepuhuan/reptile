package com.xiepuhuan.reptile.consumer.impl;

import com.xiepuhuan.reptile.consumer.CloseableBufferConsumer;
import com.xiepuhuan.reptile.model.Result;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public abstract class AbstractCloseableBufferConsumer implements CloseableBufferConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int DEFAULT_FLUSH_INTERVAL = Runtime.getRuntime().availableProcessors() * 2;

    private final Result[] buffer;

    private volatile int position;

    private final int flushInterval;

    public AbstractCloseableBufferConsumer(int flushInterval) {
        this.flushInterval = flushInterval;
        buffer = new Result[flushInterval];
    }

    public AbstractCloseableBufferConsumer() {
        this(DEFAULT_FLUSH_INTERVAL);
    }

    @Override
    public synchronized void consume(Result result) {
        buffer[position] = result;
        ++position;

        if (position == flushInterval) {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        try {
            flush(buffer, position);
        } catch (IOException e) {
            logger.warn("Failed to flush buffer: {}", e.getMessage());
        } finally {
            position = 0;
        }
    }

    protected abstract void flush(Result[] buffer, int position) throws IOException;

    public int getFlushInterval() {
        return flushInterval;
    }
}
