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
public class RedissonClientHolder {

    private static volatile RedissonClient REDISSON_CLIENT;

    private RedissonClientHolder() {}

    public static void setup(RedisConfig redisConfig) {
        if (REDISSON_CLIENT == null) {
            synchronized (RedissonClientHolder.class) {
                if (REDISSON_CLIENT == null) {
                    ArgUtils.notNull(redisConfig, "redisConfig");
                    redisConfig.check();

                    Config config = new Config().setCodec(JsonJacksonCodec.INSTANCE);
                    config.useSingleServer().setAddress(String.format("redis://%s:%d", redisConfig.getIp(), redisConfig.getPort()));
                    REDISSON_CLIENT = Redisson.create(config);
                }
            }
        }
    }

    public static RedissonClient getRedissonClient() {
        return REDISSON_CLIENT;
    }

    public static void clear() {
        if (REDISSON_CLIENT != null) {
            REDISSON_CLIENT.shutdown();
        }
    }
}
