package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.Objects;

import lombok.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author xiepuhuan
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class RedisConfig {

    public static final RedisConfig DEFAULT_REDIS_CONFIG = RedisConfig.builder().build();

    private String ip = "127.0.0.1";

    private int port = 6379;

    private String password = null;

    private int database = 0;

    private int connectionPoolSize = 64;

    public void check() {
        ArgUtils.notEmpty(ip, "redis ip");
        ArgUtils.check(port > 0 && port < 65535, "redis port　must be greater than 0 and less than 65535");
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

        return Objects.equals(this.ip, that.ip) &&
                this.port == that.port &&
                Objects.equals(this.password, that.password) &&
                this.database == that.database;
    }
}
