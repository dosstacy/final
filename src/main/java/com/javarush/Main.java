package com.javarush;

import com.javarush.config.DatabaseConfig;
import com.javarush.config.RedisConfig;
import io.lettuce.core.RedisClient;
import org.hibernate.SessionFactory;

public class Main {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    public Main() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        sessionFactory = databaseConfig.prepareRelationalDb();
        RedisConfig redisConfig = new RedisConfig();
        redisClient = redisConfig.prepareRedisClient();
    }

}
