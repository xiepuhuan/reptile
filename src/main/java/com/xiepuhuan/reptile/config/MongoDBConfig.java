package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.utils.ArgUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MongoDB数据库配置
 * @author xiepuhuan
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class MongoDBConfig {

    public static final MongoDBConfig DEFAULT_MONGODB_CONFIG = MongoDBConfig.builder().build();

    private String ip = "127.0.0.1";

    private int port = 27017;

    private String username;

    private String database = "database";

    private String password;

    private int maxConnectionSize = 100;

    private int minConnectionSize = 0;

    public void check() {
        ArgUtils.notEmpty(ip, "mongodb ip");
        ArgUtils.notEmpty(database, "database");
        ArgUtils.check(port > 0 && port < 65535, "mongodb port must be greater than 0 and less than 65535");
        ArgUtils.check(maxConnectionSize > 0, "maxConnectionSize must be greater than 0");
        ArgUtils.check(minConnectionSize >= 0, "minConnectionSize must be greater than or equal 0");
    }
}
