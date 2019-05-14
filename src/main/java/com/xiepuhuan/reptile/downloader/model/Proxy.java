package com.xiepuhuan.reptile.downloader.model;

import com.xiepuhuan.reptile.utils.ArgUtils;
import org.apache.http.HttpHost;

/**
 * @author xiepuhuan
 */
public class Proxy {

    public static final String DEFAULT_SCHEME_NAME = "http";

    private String schemeName;

    private final String host;

    private final int port;

    private String username;

    private String password;

    public Proxy(String host, int port) {
        this(host, port, DEFAULT_SCHEME_NAME);
    }

    public Proxy(String host, int port, String schemeName) {
        ArgUtils.notEmpty(host, "host");
        ArgUtils.notEmpty(schemeName, "schemeName");
        this.host = host;
        this.port = port;
        this.schemeName = schemeName;
    }

    public Proxy setUsername(String username) {
        this.username = username;
        return this;
    }

    public Proxy setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public HttpHost buildHttpHost() {
        return new HttpHost(host, port, schemeName);
    }
}
