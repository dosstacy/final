package com.javarush.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.util.SafeEncoder;

public class RedisRepository {
    private final UnifiedJedis jedis;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPK_NAME = "city-country";
    private static final int THRESHOLD = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
    private static final String REDIS_URL = "redis://127.0.0.1:6379";
    private static final String BRACES_REGEX = "[\\[\\]]";

    public RedisRepository() {
        jedis = new UnifiedJedis(REDIS_URL);

        jedis.del(TOPK_NAME);
        if (!jedis.exists(TOPK_NAME)) {
            jedis.topkReserve(TOPK_NAME, 5L, 2000L, 7L, 0.925D);
        }
    }

    public <T> T getById(String key, Class<T> clazz) throws JsonProcessingException {
        String serializedEntity = jedis.get(key);
        return deserialize(serializedEntity, clazz);
    }

    public <T> void put(String key, T value) throws JsonProcessingException {
        jedis.topkAdd(TOPK_NAME, key);

        if (getCount(key) >= THRESHOLD) {
            jedis.set(key, serialize(value));
        }
    }

    private int getCount(String key) {
        String count = jedis.sendCommand(() -> SafeEncoder.encode("TOPK.COUNT"), SafeEncoder.encodeMany(TOPK_NAME, key)).toString();
        return Integer.parseInt(count.replaceAll(BRACES_REGEX, ""));
    }

    public boolean checkExists(String key) {
        return jedis.exists(key);
    }

    private <T> String serialize(T entity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entity);
    }

    private <T> T deserialize(String serializedEntity, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(serializedEntity, clazz);
    }
}
