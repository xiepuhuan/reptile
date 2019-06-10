package com.xiepuhuan.reptile.common.redis;

import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.utils.ArgUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * @author xiepuhuan
 */
public class RedisClientManager {

    private RedisClientManager() {}

    public static RedissonClient createRedissonClient(RedisConfig redisConfig) {
        ArgUtils.notNull(redisConfig, "redisConfig");
        redisConfig.check();

        Config config = new Config().setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", redisConfig.getIp(), redisConfig.getPort()))
                .setDatabase(redisConfig.getDatabase())
                .setPassword(redisConfig.getPassword())
                .setConnectionPoolSize(redisConfig.getConnectionPoolSize());
        return Redisson.create(config);
    }
}
