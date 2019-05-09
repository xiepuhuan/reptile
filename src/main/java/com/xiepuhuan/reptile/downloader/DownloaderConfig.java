package com.xiepuhuan.reptile.downloader;

import java.util.List;
import org.apache.http.Header;

/**
 * @author xiepuhuan
 */
public class DownloaderConfig {

    public static final DownloaderConfig DEFAULT_DOWNLOADER_CONFIG = DownloaderConfigBuilder.create();

    private String userAgent;

    private String proxyHost;

    private int proxyPort;

    private String proxyUsername;

    private String proxyPassword;

    private int defaultMaxPerRoute;

    private int maxTotalConnections;

    private int socketTimeout;

    private int connectTimeout;

    private List<Header> headers;

    DownloaderConfig(String userAgent, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword,
                            int defaultMaxPerRoute, int maxTotalConnections, int socketTimeout, int connectTimeout) {
        this.userAgent = userAgent;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotalConnections = maxTotalConnections;
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
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

    public List<Header> getHeaders() {
        return headers;
    }
}
