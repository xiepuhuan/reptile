package com.xiepuhuan.reptile.consumer;

import com.xiepuhuan.reptile.model.Result;

/**
 * 数据消费者接口
 */
public interface Consumer {

    /**
     * 消费处理结果数据
     * @param result
     * @throws Throwable
     */
    void consume(Result result) throws Throwable;
}
