package com.xiepuhuan.reptile.handler;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import java.util.List;

/**
 * 爬取的响应内容处理器
 */
public interface ResponseHandler {

     /**
      * 处理响应内容
      * @param responseContext 下载器爬取解析生成的响应内容上下文
      * @param result 保存此次处理后的结果
      * @return 从响应内容解析出新的爬取请求
      * @throws Throwable
      */
     List<Request> handle(ResponseContext responseContext, Result result) throws Throwable;

     /**
      * 判断是否支持处理该响应内容
      * @param responseContext
      * @return 支不支持处理
      */
     boolean isSupport(ResponseContext responseContext);
}
