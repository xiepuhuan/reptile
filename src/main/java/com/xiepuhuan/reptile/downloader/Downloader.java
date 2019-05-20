package com.xiepuhuan.reptile.downloader;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import java.io.IOException;

public interface Downloader {

    Response download(Request request) throws IOException;
}
