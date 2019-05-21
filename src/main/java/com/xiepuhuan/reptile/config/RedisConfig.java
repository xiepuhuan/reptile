package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.Objects;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author xiepuhuan
 */
public final class RedisConfig {

    public static final RedisConfig DEFAULT_REDIS_CONFIG = Builder.create();

    private String ip;

    private int port;

    private String password;

    private int database;

    private int connectionPoolSize;

    public void check() {
        ArgUtils.notEmpty(ip, "redis ip");
        ArgUtils.check(port > 0 && port < 65535, "redis port　must be greater than 0 and less than 65535");
    }

    private RedisConfig(String ip, int port, String password, int database, int connectionPoolSize) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.database = database;
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "connectionPoolSize");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RedisConfig)) {
            return false;
        }

        RedisConfig that = (RedisConfig) obj;

        return Objects.equals(this.ip, that.ip) && this.port == that.port &&
                Objects.equals(this.password, that.password) && this.database == that.database;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RedisConfig{");
        sb.append("ip='").append(ip).append('\'');
        sb.append(", port=").append(port);
        sb.append(", password='").append(password).append('\'');
        sb.append(", database=").append(database);
        sb.append(", connectionPoolSize=").append(connectionPoolSize);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {

        private String ip;

        private int port;

        private String password;

        private int database;

        private int connectionPoolSize;

        public Builder() {
            this.ip = "127.0.0.1";
            this.port = 6379;
            this.password = null;
            this.database = 0;
            this.connectionPoolSize = 64;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDatabase(int database) {
            this.database = database;
            return this;
        }

        public Builder setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
            return this;
        }

        public static Builder custom() {
            return new Builder();
        }

        public RedisConfig build() {
            return new RedisConfig(ip, port, password, database, connectionPoolSize);
        }

        public static RedisConfig create() {
            return custom().build();
        }
    }
}
