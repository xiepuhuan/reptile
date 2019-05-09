package com.xiepuhuan.reptile.consumer.impl;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.model.Result;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiepuhuan
 */
public class ConsoleConsumer implements Consumer {

    private final AtomicInteger dataNumber;

    public ConsoleConsumer() {
        this.dataNumber =  new AtomicInteger(1);
    }

    @Override
    public void consume(Result result) {
        System.out.println(String.format("Consuming the %dth result", dataNumber.getAndIncrement()));
        System.out.println(result);
    }
}
