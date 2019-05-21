package com.xiepuhuan.reptile.consumer;

import com.xiepuhuan.reptile.model.Result;

public interface Consumer {

    void consume(Result result) throws Throwable;
}
