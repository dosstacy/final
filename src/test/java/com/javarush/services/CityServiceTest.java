package com.javarush.services;

import com.javarush.DataTransformer;
import com.javarush.cache.RedisRepository;
import com.javarush.domain.entity.City;
import com.javarush.domain.exceptions.CityException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CityServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    private City city;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        city = new City();
    }

    @Test
    void testGetById_CityExistsInRedis() {
        int id = 1;
        String key = "city_" + id;
        CityCountry testCityCountry = DataTransformer.cityTransformToCityCountry(city);

        when(redisRepository.checkExists(key)).thenReturn(true);
        when(redisRepository.getById(key, CityCountry.class)).thenReturn(testCityCountry);

        City city = cityService.getById(id);

        assertNotNull(city);
        assertEquals(city.getId(), city.getId());
        verify(redisRepository, times(1)).getById(key, CityCountry.class);
    }

    @Test
    void testGetById_CityNotInRedis() throws Exception {
        int cityId = 1;
        String key = "city_" + cityId;

        when(redisRepository.checkExists(key)).thenReturn(false);
        when(cityRepository.getById(cityId)).thenReturn(city);

        City result = cityService.getById(cityId);

        assertNotNull(result);
        verify(redisRepository, times(1)).put(eq(key), any(CityCountry.class));
        assertEquals(city, result);
    }

    @Test
    void testGetById_ExceptionThrown() {
        int cityId = 1;
        String key = "city_" + cityId;

        when(redisRepository.checkExists(key)).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(CityException.class, () -> cityService.getById(cityId));
        assertTrue(exception.getMessage().contains("ERROR :: cannot get city with id"));
    }

    @Test
    void testSave_Successful() throws Exception {
        when(cityRepository.save(city)).thenReturn(city);

        City result = cityService.save(city);

        assertNotNull(result);
        assertEquals(city, result);
    }

    @Test
    void testSave_ExceptionThrown() {
        when(cityRepository.save(city)).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(CityException.class, () -> cityService.save(city));
        assertTrue(exception.getMessage().contains("ERROR :: cannot save city with id"));
    }

    @Test
    void testDelete_Successful() throws Exception {
        int cityId = 1;

        doNothing().when(cityRepository).delete(cityId);

        assertDoesNotThrow(() -> cityService.delete(cityId));
    }

    @Test
    void testDelete_ExceptionThrown() {
        int cityId = 1;

        doThrow(new RuntimeException("Test exception")).when(cityRepository).delete(cityId);

        Exception exception = assertThrows(CityException.class, () -> cityService.delete(cityId));
        assertTrue(exception.getMessage().contains("ERROR :: cannot delete city with id"));
    }
}