package com.javarush.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.redis.CityCountry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RedisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);
    private final RedisClient redisClient;
    private final ObjectMapper mapper;

    public RedisService(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.mapper = new ObjectMapper();
    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Failed push to redis. Error message :: {}", e.getMessage());
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error message :: {}", e.getMessage());
                    e.printStackTrace(System.out);
                }
            }
        }catch(Exception e) {
            LOGGER.error("Error connecting to Redis. Error Message :: {}", e.getMessage());
        }
    }
}
