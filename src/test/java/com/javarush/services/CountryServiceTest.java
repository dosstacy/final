package com.javarush.services;

import com.javarush.DataTransformer;
import com.javarush.cache.RedisRepository;
import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.CountryException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CountryServiceTest {
    @Mock
    private RedisRepository redisRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private Country country;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        country = new Country();
    }

    @Test
    void testGetById_CountryExistsInRedis() {
        int id = 1;
        String key = "country_" + id;
        CityCountry testCityCountry = DataTransformer.countryTransformToCityCountry(country);

        when(redisRepository.checkExists(key)).thenReturn(true);
        when(redisRepository.getById(key, CityCountry.class)).thenReturn(testCityCountry);

        Country country1 = countryService.getById(id);

        assertNotNull(country1);
        assertEquals(country1, country);
        verify(redisRepository, times(1)).getById(key, CityCountry.class);
    }

    @Test
    void testGetById_CountryNotInRedis() throws Exception {
        int countryId = 1;
        String key = "country_" + countryId;

        when(redisRepository.checkExists(key)).thenReturn(false);
        when(countryRepository.getById(countryId)).thenReturn(country);

        Country result = countryService.getById(countryId);

        assertNotNull(result);
        verify(redisRepository, times(1)).put(eq(key), any(CityCountry.class));
        assertEquals(country, result);
    }

    @Test
    void testGetById_ExceptionThrown() {
        int countryId = 1;
        String key = "country_" + countryId;

        when(redisRepository.checkExists(key)).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(CountryException.class, () -> countryService.getById(countryId));
        assertTrue(exception.getMessage().contains("ERROR :: cannot get country with id"));
    }

    @Test
    void testSave_Successful() throws Exception {
        when(countryRepository.save(country)).thenReturn(country);

        Country result = countryService.save(country);

        assertNotNull(result);
        assertEquals(country, result);
    }

    @Test
    void testSave_ExceptionThrown() {
        when(countryRepository.save(country)).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(CountryException.class, () -> countryService.save(country));
        assertTrue(exception.getMessage().contains("ERROR :: could not save country with id"));
    }

    @Test
    void testDelete_Successful() throws Exception {
        int countryId = 1;

        doNothing().when(countryRepository).delete(countryId);

        assertDoesNotThrow(() -> countryService.delete(countryId));
    }

    @Test
    void testDelete_ExceptionThrown() {
        int countryId = 1;

        doThrow(new RuntimeException("Test exception")).when(countryRepository).delete(countryId);

        Exception exception = assertThrows(CountryException.class, () -> countryService.delete(countryId));
        assertTrue(exception.getMessage().contains("ERROR :: cannot delete country with id"));
    }
}