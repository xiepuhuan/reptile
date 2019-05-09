package com.xiepuhuan.reptile.downloader;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

/**
 * @author xiepuhuan
 */
public class DownloaderConfigBuilder {

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

    public DownloaderConfigBuilder() {
        this.userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36";
        this.proxyHost = null;
        this.defaultMaxPerRoute = 100;
        this.maxTotalConnections = 100;
        this.socketTimeout = 5000;
        this.connectTimeout = 5000;
        this.headers = new ArrayList<>();
    }

    public DownloaderConfigBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public DownloaderConfigBuilder setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    public DownloaderConfigBuilder setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public DownloaderConfigBuilder setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
        return this;
    }

    public DownloaderConfigBuilder setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }

    public DownloaderConfigBuilder setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        return this;
    }

    public DownloaderConfigBuilder setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
        return this;
    }

    public DownloaderConfigBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public DownloaderConfigBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public DownloaderConfigBuilder setHeaders(Header header) {
        this.headers.add(header);
        return this;
    }

    public DownloaderConfigBuilder setHeader(String name, String value) {
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

    public static DownloaderConfigBuilder custom() {
        return new DownloaderConfigBuilder();
    }

    public DownloaderConfig build() {
        return new DownloaderConfig(userAgent, proxyHost, proxyPort, proxyUsername, proxyPassword,
                defaultMaxPerRoute, maxTotalConnections, socketTimeout, connectTimeout);
    }

    public static DownloaderConfig create() {
        return custom().build();
    }
}
