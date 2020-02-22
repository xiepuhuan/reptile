package com.xiepuhuan.reptile.config;

import com.google.common.collect.Lists;
import com.xiepuhuan.reptile.downloader.constants.UserAgentConstants;
import com.xiepuhuan.reptile.downloader.model.Proxy;
import com.xiepuhuan.reptile.model.Cookie;
import com.xiepuhuan.reptile.model.Header;
import com.xiepuhuan.reptile.utils.ArgUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author xiepuhuan
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloaderConfig {

    public static final DownloaderConfig DEFAULT_DOWNLOADER_CONFIG = DownloaderConfig.builder().build();

    /**
     * 通用的user-agent, 对所有HTTP请求的首部行设置该值
     */
    private String generalUserAgent = UserAgentConstants.CHROME_FOR_LINUX;

    private PoolConfig<String> userAgentPoolConfig;

    private boolean enableUserAgentPoolConfig;

    /**
     * 通用的proxy, 对所有HTTP请求生效
     */
    private Proxy generalProxy;

    private PoolConfig<Proxy> proxyPoolConfig;

    private boolean enableProxyPoolConfig;

    private int defaultMaxPerRoute = 100;

    private int maxTotalConnections = 200;

    /**
     * 从socket中读取数据的超时时间
     */
    private int socketTimeout = 2000;

    /**
     * 建立连接超时时间
     *
     */
    private int connectTimeout = 2000;

    /**
     * 从连接池中获取连接超时时间
     */
    private int connectRequestTimeout = 500;

    private List<Header> headers = Lists.newArrayList();

    private List<Cookie> cookies = Lists.newArrayList();

    public void check() {
        ArgUtils.notEmpty(generalUserAgent, "generalUserAgent");
    }
}
