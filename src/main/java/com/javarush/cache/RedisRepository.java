package com.javarush.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.cache.redis_enum.RedisModuleCommand;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class RedisRepository<T> {
    private final Jedis jedis;
    private final ObjectMapper objectMapper;
    private static final String TOP_K_NAME = "topk_queries";

    public RedisRepository() {
        this.objectMapper = new ObjectMapper();
        this.jedis = new Jedis();
        jedis.sendCommand(RedisModuleCommand.RESERVE, TOP_K_NAME, "500");
    }

    public void save(String key, T entity) {
        jedis.set(key, serialize(entity));
    }

    public T getById(String key, Class<T> clazz) { //?
        String serializedEntity = jedis.get(key);
        if (serializedEntity != null) {
            jedis.sendCommand(RedisModuleCommand.ADD, TOP_K_NAME, key);
            return deserialize(serializedEntity, clazz);
        }
        return null;
    }

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
    }

    public List<String> getTopKQueries(int k) {
        Object object = jedis.sendCommand(RedisModuleCommand.LIST, TOP_K_NAME, String.valueOf(k));
        if (object instanceof List) {
            return (List<String>) object;
        }
        System.out.println("cannot cast object to list");
        return null;
    }

    private String serialize(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            System.err.println("Error serializing object: " + e.getMessage());
            return null;
        }
    }

    private T deserialize(String serializedEntity, Class<T> clazz) {
        try {
            return objectMapper.readValue(serializedEntity, clazz);
        } catch (Exception e) {
            System.err.println("Error deserializing object: " + e.getMessage());
            return null;
        }
    }
}
