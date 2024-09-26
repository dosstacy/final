package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.redis.CityCountry;
import com.javarush.services.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class RedisServiceTest {
    @Mock
    private RedisClient redisClient;

    @Mock
    private StatefulRedisConnection<String, String> connection;

    @Mock
    private RedisCommands<String, String> syncCommands;

    @InjectMocks
    private RedisService redisService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisClient.connect()).thenReturn(connection);
        when(connection.sync()).thenReturn(syncCommands);
    }

    @Test
    public void testPushToRedis() throws JsonProcessingException {
        CityCountry cityCountry = new CityCountry();
        cityCountry.setId(1);
        cityCountry.setName("Test City");

        List<CityCountry> data = List.of(cityCountry);

        redisService.pushToRedis(data);

        verify(syncCommands, times(1)).set(eq("1"), eq(mapper.writeValueAsString(cityCountry)));
    }

    @Test
    public void testTestRedisData() throws JsonProcessingException {
        CityCountry cityCountry = new CityCountry();
        cityCountry.setId(1);
        cityCountry.setName("Test City");

        String cityCountryJson = mapper.writeValueAsString(cityCountry);
        when(syncCommands.get("1")).thenReturn(cityCountryJson);

        List<Integer> ids = List.of(1);

        redisService.testRedisData(ids);

        verify(syncCommands, times(1)).get("1");
    }
}