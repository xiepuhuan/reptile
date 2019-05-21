package com.xiepuhuan.reptile.config;

import com.xiepuhuan.reptile.utils.ArgUtils;

/**
 * @author xiepuhuan
 */
public final class MongoDBConfig {

    public static final MongoDBConfig DEFAULT_MONGODB_CONFIG = MongoDBConfig.Builder.create();

    private String ip;

    private int port;

    private String username;

    private String database;

    private String password;

    private int maxConnectionSize;

    private int minConnectionSize;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getDatabase() {
        return database;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxConnectionSize() {
        return maxConnectionSize;
    }

    public int getMinConnectionSize() {
        return minConnectionSize;
    }

    private MongoDBConfig(String ip, int port, String username, String database, String password, int maxConnectionSize, int minConnectionSize) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.database = database;
        this.password = password;
        this.maxConnectionSize = maxConnectionSize;
        this.minConnectionSize = minConnectionSize;
    }

    public void check() {
        ArgUtils.notEmpty(ip, "mongodb ip");
        ArgUtils.notEmpty(database, "database");
        ArgUtils.check(port > 0 && port < 65535, "mongodb port must be greater than 0 and less than 65535");
        ArgUtils.check(maxConnectionSize > 0, "maxConnectionSize must be greater than 0");
        ArgUtils.check(minConnectionSize >= 0, "minConnectionSize must be greater than or equal 0");
    }

    public static class Builder {
        private String ip;

        private int port;

        private String username;

        private String database;

        private String password;

        private int maxConnectionSize;

        private int minConnectionSize;

        private Builder() {
            this.ip = "127.0.0.1";
            this.port = 27017;
            this.username = null;
            this.database = "database";
            this.password = null;
            this.maxConnectionSize = 100;
            this.minConnectionSize = 0;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setMaxConnectionSize(int maxConnectionSize) {
            this.maxConnectionSize = maxConnectionSize;
            return this;
        }

        public Builder setMinConnectionSize(int minConnectionSize) {
            this.minConnectionSize = minConnectionSize;
            return this;
        }

        public static MongoDBConfig.Builder custom() {
            return new MongoDBConfig.Builder();
        }

        public MongoDBConfig build() {
            return new MongoDBConfig(ip, port, username, database, password, maxConnectionSize, minConnectionSize);
        }

        public static MongoDBConfig create() {
            return custom().build();
        }
    }
}
