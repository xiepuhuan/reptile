package com.xiepuhuan.reptile.downloader;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import java.io.IOException;

/**
 * 下载器接口
 */
public interface Downloader {

    /**
     * 根据请求下载响应
     * @param request 请求内容
     * @return
     * @throws IOException
     */
    Response download(Request request) throws IOException;
}
