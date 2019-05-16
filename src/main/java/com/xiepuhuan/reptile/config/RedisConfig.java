package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.utils.ArgUtils;

/**
 * @author xiepuhuan
 */
public class RedisConfig {

    public static final RedisConfig DEFAULT_REDIS_CONFIG = Builder.create();

    private String ip;

    private int port;

    public void check() {
        ArgUtils.notEmpty(ip, "redis ip");
        ArgUtils.check(port > 0 && port < 65535, "redis port");
    }

    private RedisConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public static class Builder {
        private String ip;

        private int port;

        public Builder() {
            this.ip = "127.0.0.1";
            this.port = 6379;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public static Builder custom() {
            return new Builder();
        }

        public RedisConfig build() {
            return new RedisConfig(ip, port);
        }

        public static RedisConfig create() {
            return custom().build();
        }
    }
}
