package com.javarush.cache;

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

    public RedisRepository() {
        jedis = new UnifiedJedis("redis://127.0.0.1:6379");

        jedis.del(TOPK_NAME);
        if (!jedis.exists(TOPK_NAME)) {
            jedis.topkReserve(TOPK_NAME, 5L, 2000L, 7L, 0.925D);
        }
    }

    public <T> T getById(String key, Class<T> clazz) {
        String serializedEntity = jedis.get(key);
        return deserialize(serializedEntity, clazz);
    }

    public <T> void put(String key, T value) {
        jedis.topkAdd(TOPK_NAME, key);

        if (getCount(key) >= THRESHOLD) {
            System.out.println(serialize(value));
            jedis.set(key, serialize(value));
        }
    }

    private int getCount(String key) {
        String count = jedis.sendCommand(() -> SafeEncoder.encode("TOPK.COUNT"), SafeEncoder.encodeMany(TOPK_NAME, key)).toString();
        return Integer.parseInt(count.replaceAll("[\\[\\]]", ""));
    }

    public boolean checkExists(String key) {
        return jedis.exists(key);
    }

    private <T> String serialize(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            LOGGER.error("Error serializing object: " + e.getMessage());
            return null;
        }
    }

    private <T> T deserialize(String serializedEntity, Class<T> clazz) {
        try {
            return objectMapper.readValue(serializedEntity, clazz);
        } catch (Exception e) {
            LOGGER.error("Error deserializing object: " + e.getMessage());
            return null;
        }
    }
}

/*public void save(String key, T entity) {
       jedis.set(key, serialize(entity));

public void delete(String key) {
        jedis.del(key);
    }

    public List<T> getAll(String listName, Class<T> clazz) {
        List<String> str = jedis.lrange(listName, 0, -1);
        List<T> typeClass = new ArrayList<>();
        for (String s : str) {
            typeClass.add(deserialize(s, clazz));
        }
        return typeClass;
    }

    public List<T> getItems(String listName, int offset, int limit, Class<T> clazz) {
        List<String> str = jedis.lrange(listName, offset, limit - 1);
        List<T> typeClass = new ArrayList<>();
        for (String s : str) {
            typeClass.add(deserialize(s, clazz));
        }
        return typeClass;
    }

    public int getTotalCount(String listName) {
        return Math.toIntExact(jedis.llen(listName));
    }*/
