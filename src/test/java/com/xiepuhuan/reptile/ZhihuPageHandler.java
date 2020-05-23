package com.xiepuhuan.reptile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.constants.DeploymentModeEnum;
import com.xiepuhuan.reptile.consumer.impl.ConsoleConsumer;
import com.xiepuhuan.reptile.handler.ResponseHandler;
import com.xiepuhuan.reptile.handler.impl.ResponseHandlerChain;
import com.xiepuhuan.reptile.model.Content;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.ResponseContext;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.scheduler.impl.FIFOQueueScheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiepuhuan
 */
public class ZhihuPageHandler implements ResponseHandler {

    private static String[] URLS = new String[] {
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=java&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=php&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=c++&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=go&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=python&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=ruby&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=c#&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=tomcat&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=京东&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=腾讯&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=阿里&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=百度&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=滴滴&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=美团&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=网易&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0",
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=快手&correction=1&offset=20&limit=20&lc_idx=26&show_all_topics=0"
    };

    @Override
    public List<Request> handle(ResponseContext responseContext, Result result) {
        Content content = responseContext.getResponse().getContent();
        JSONObject jsonObject = JSON.parseObject(content.getContent(), JSONObject.class);
        result.setResults(jsonObject.getInnerMap());

        JSONObject paging = jsonObject.getJSONObject("paging");

        if (!paging.getBoolean("is_end")) {
            List<Request> requests = new ArrayList<>();
            requests.add(new Request(paging.getString("next")));
            return requests;
        }
        return null;
    }

    @Override
    public boolean isSupport(ResponseContext responseContext) {
        return true;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ReptileConfig config = ReptileConfig.builder()
                .asynRun(false)
                .threadCount(8)
                .responseHandlerChain(ResponseHandlerChain.create().addResponseHandler(new ZhihuPageHandler()))
                .deploymentMode(DeploymentModeEnum.SINGLE)
                .scheduler(new FIFOQueueScheduler())
                .consumer(new ConsoleConsumer())
                .build();
        Reptile reptile = Reptile.create(config).addUrls(URLS);
        reptile.start();
        System.out.println(System.currentTimeMillis() - start);
    }
}