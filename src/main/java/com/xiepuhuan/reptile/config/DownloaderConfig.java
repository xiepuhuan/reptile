package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.downloader.constants.UserAgentConstants;
import com.xiepuhuan.reptile.downloader.model.Proxy;
import com.xiepuhuan.reptile.model.Cookie;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.impl.cookie.BasicClientCookie2;

/**
 * @author xiepuhuan
 */
public class DownloaderConfig {

    public static final DownloaderConfig DEFAULT_DOWNLOADER_CONFIG = Builder.create();

    /**
     * 通用的user-agent, 对所有HTTP请求的首部行设置该值
     */
    private String generalUserAgent;

    private PoolConfig<String> userAgentPoolConfig;

    private boolean enableUserAgentPoolConfig;

    /**
     * 通用的proxy, 对所有HTTP请求生效
     */
    private Proxy generalProxy;

    private PoolConfig<Proxy> proxyPoolConfig;

    private boolean enableProxyPoolConfig;

    private int defaultMaxPerRoute;

    private int maxTotalConnections;

    /**
     * 从socket中读取数据的超时时间
     */
    private int socketTimeout;

    /**
     * 建立连接超时时间
     *
     */
    private int connectTimeout;

    /**
     * 从连接池中获取连接超时时间
     */
    private int connectRequestTimeout;

    private List<Header> headers;

    private List<Cookie> cookies;

    public void check() {
        ArgUtils.notEmpty(generalUserAgent, "generalUserAgent");

    }

    private DownloaderConfig(String generalUserAgent, PoolConfig<String> userAgentPoolConfig, boolean enableUserAgentPoolConfig,
                            Proxy generalProxy, PoolConfig<Proxy> proxyPoolConfig, boolean enableProxyPoolConfig,
                            int defaultMaxPerRoute, int maxTotalConnections, int socketTimeout, int connectTimeout,
                            int connectRequestTimeout, List<Header> headers, List<Cookie> cookies) {
        this.generalUserAgent = generalUserAgent;
        this.userAgentPoolConfig = userAgentPoolConfig;
        this.enableUserAgentPoolConfig = enableUserAgentPoolConfig;
        this.generalProxy = generalProxy;
        this.proxyPoolConfig = proxyPoolConfig;
        this.enableProxyPoolConfig = enableProxyPoolConfig;
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotalConnections = maxTotalConnections;
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
        this.connectRequestTimeout = connectRequestTimeout;
        this.headers = headers;
        this.cookies = cookies;
    }

    public String getGeneralUserAgent() {
        return generalUserAgent;
    }

    public PoolConfig<String> getUserAgentPoolConfig() {
        return userAgentPoolConfig;
    }

    public boolean isEnableUserAgentPoolConfig() {
        return enableUserAgentPoolConfig;
    }

    public Proxy getGeneralProxy() {
        return generalProxy;
    }

    public PoolConfig<Proxy> getProxyPoolConfig() {
        return proxyPoolConfig;
    }

    public boolean isEnableProxyPoolConfig() {
        return enableProxyPoolConfig;
    }

    public int getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getConnectRequestTimeout() {
        return connectRequestTimeout;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public static class Builder {

        private String generalUserAgent;

        private PoolConfig<String> userAgentPoolConfig;

        private Proxy generalProxy;

        private PoolConfig<Proxy> proxyPoolConfig;

        private int defaultMaxPerRoute;

        private int maxTotalConnections;

        private int socketTimeout;

        private int connectTimeout;

        private int connectRequestTimeout;

        private List<Header> headers;

        private List<Cookie> cookies;

        Builder() {
            this.generalUserAgent = UserAgentConstants.CHROME_FOR_LINUX;
            this.userAgentPoolConfig = null;
            this.generalProxy = null;
            this.proxyPoolConfig = null;
            this.defaultMaxPerRoute = 100;
            this.maxTotalConnections = 200;
            this.socketTimeout = 2000;
            this.connectTimeout = 2000;
            this.connectRequestTimeout = 500;
            this.headers = new ArrayList<>();
            this.cookies = new ArrayList<>();
        }

        public Builder setGeneralUserAgent(String generalUserAgent) {
            this.generalUserAgent = generalUserAgent;
            return this;
        }

        public Builder setUserAgentPoolConfig(PoolConfig<String> userAgentPoolConfig) {
            this.userAgentPoolConfig = userAgentPoolConfig;
            return this;
        }

        public Builder setGeneralProxy(Proxy generalProxy) {
            this.generalProxy = generalProxy;
            return this;
        }

        public Builder setProxyPoolConfig(PoolConfig<Proxy> proxyPoolConfig) {
            this.proxyPoolConfig = proxyPoolConfig;
            return this;
        }

        public Builder setDefaultMaxPerRoute(int defaultMaxPerRoute) {
            this.defaultMaxPerRoute = defaultMaxPerRoute;
            return this;
        }

        public Builder setMaxTotalConnections(int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
            return this;
        }

        public Builder setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setConnectRequestTimeout(int connectRequestTimeout) {
            this.connectRequestTimeout = connectRequestTimeout;
            return this;
        }

        public Builder appendHeaders(Collection<Header> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public Builder appendHeader(String name, String value) {
            this.headers.add(new Header() {
                @Override
                public HeaderElement[] getElements() throws ParseException {
                    return new HeaderElement[0];
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getValue() {
                    return value;
                }
            });
            return this;
        }

        public Builder appendCookie(Cookie cookie) {
            this.cookies.add(cookie);
            return this;
        }

        public Builder appendCookies(Cookie... cookies) {
            this.cookies.addAll(Arrays.asList(cookies));
            return this;
        }

        public Builder appendCookies(Collection<Cookie> cookies) {
            this.cookies.addAll(cookies);
            return this;
        }

        public static Builder custom() {
            return new Builder();
        }

        public DownloaderConfig build() {
            boolean enableUserAgentPoolConfig = Objects.nonNull(userAgentPoolConfig) && CollectionUtils.isNotEmpty(userAgentPoolConfig.getPool());
            boolean enableProxyPoolConfig = Objects.nonNull(proxyPoolConfig) && CollectionUtils.isNotEmpty(proxyPoolConfig.getPool());

            return new DownloaderConfig(generalUserAgent, userAgentPoolConfig, enableUserAgentPoolConfig, generalProxy,
                    proxyPoolConfig, enableProxyPoolConfig, defaultMaxPerRoute, maxTotalConnections, socketTimeout,
                    connectTimeout, connectRequestTimeout, headers, cookies);
        }

        public static DownloaderConfig create() {
            return custom().build();
        }
    }
}
