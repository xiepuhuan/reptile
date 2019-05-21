package com.xiepuhuan.reptile.consumer.impl;

import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.model.Result;

/**
 * @author xiepuhuan
 */
public class ConsoleConsumer implements Consumer {

    private int dataNumber;

    public ConsoleConsumer() {
        this.dataNumber = 1;
    }

    @Override
    public synchronized void consume(Result result) {
        System.out.println(String.format("Consuming the %dth result\n", dataNumber++) + result.getResults());
    }
}
