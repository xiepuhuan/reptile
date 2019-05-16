package com.xiepuhuan.reptile.consumer;

import com.xiepuhuan.reptile.model.Result;
import java.io.IOException;

public interface Consumer {

    void consume(Result result) throws IOException;
}
