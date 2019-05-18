package com.xiepuhuan.reptile.common.redis;

import com.xiepuhuan.reptile.config.RedisConfig;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * @author xiepuhuan
 */
public class RedisClientManager {

    private static final Map<RedisConfig, RedissonClient> REDISSON_CLIENT_MAP = new ConcurrentHashMap<>();

    private RedisClientManager() {}

    public static RedissonClient getRedissonClient(RedisConfig redisConfig) {
        ArgUtils.notNull(redisConfig, "redisConfig");
        redisConfig.check();

        RedissonClient redissonClient;
        if ((redissonClient = REDISSON_CLIENT_MAP.get(redisConfig)) == null) {
            synchronized (RedisClientManager.class) {
                if ((redissonClient = REDISSON_CLIENT_MAP.get(redisConfig)) == null) {
                    Config config = new Config().setCodec(JsonJacksonCodec.INSTANCE);
                    config.useSingleServer()
                            .setAddress(String.format("redis://%s:%d", redisConfig.getIp(), redisConfig.getPort()))
                            .setDatabase(redisConfig.getDatabase())
                            .setPassword(redisConfig.getPassword())
                            .setConnectionPoolSize(redisConfig.getConnectionPoolSize());
                    REDISSON_CLIENT_MAP.put(redisConfig, (redissonClient = Redisson.create(config)));
                }
            }
        }
        return redissonClient;
    }

    public static void shutdownRedisClient(RedisConfig redisConfig) {
        ArgUtils.notNull(redisConfig, "redisConfig");
        redisConfig.check();

        RedissonClient redissonClient;
        if ((redissonClient = REDISSON_CLIENT_MAP.get(redisConfig)) != null) {
            synchronized (RedisClientManager.class) {
                if ((redissonClient = REDISSON_CLIENT_MAP.get(redisConfig)) != null) {
                    redissonClient.shutdown();
                    REDISSON_CLIENT_MAP.remove(redisConfig);
                }
            }
        }
    }
}
