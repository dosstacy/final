package com.javarush.services;

import com.javarush.DataTransformer;
import com.javarush.cache.RedisRepository;
import com.javarush.domain.entity.City;
import com.javarush.domain.exceptions.CityException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
public class CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
    private final RedisRepository redisRepository;
    private final CityRepository cityRepository;

    public City getById(Integer id) {
        String key = "city_" + id;
        try {
            if(redisRepository.checkExists(key)){
                CityCountry cityCountry = redisRepository.getById(key, CityCountry.class);
                return DataTransformer.cityCountryTransformToCity(cityCountry);
            }
            City city = cityRepository.getById(id);
            redisRepository.put(key, DataTransformer.cityTransformToCityCountry(city));
            return city;
        } catch (Exception e) {
            LOGGER.error("Cannot get city with id : {} ", id, e);
            throw new CityException("ERROR :: cannot get city with id " + id + " " + e.getMessage());
        }
    }

    public City save(City entity) {
        try {
            City city = new CityRepository().save(entity);
            if (city == null) {
                LOGGER.error("No city with id {}", entity.getId());
                throw new CityException("ERROR :: no city with id " + entity.getId());
            }
            return city;
        } catch (Exception e) {
            LOGGER.error("Cannot save city with id {}", entity.getId(), e);
            throw new CityException("ERROR :: cannot save city with id " + entity.getId() + " " + e.getMessage());
        }
    }

    public void delete(Integer id) {
        try {
            new CityRepository().delete(id);
        } catch (Exception e) {
            LOGGER.error("Cannot delete city with id: {}", id, e);
            throw new CityException("ERROR :: cannot delete city with id: " + id + " " + e.getMessage());
        }
    }

    public List<City> getAll() {
        try {
            List<City> cities = new CityRepository().getAll();
            if (cities.isEmpty()) {
                LOGGER.error("No cities found");
                throw new CityException("ERROR :: no cities found");
            }
            return cities;
        } catch (Exception e) {
            LOGGER.error("Cannot get all cities : ", e);
            throw new CityException("ERROR :: cannot get all cities: " + e.getMessage());
        }
    }

    public List<City> getItems(int offset, int limit) {
        try {
            List<City> cities = new CityRepository().getItems(offset, limit);
            if (cities.isEmpty()) {
                LOGGER.error("No cities found in range");
                throw new CityException("ERROR :: no cities found in range");
            }
            return cities;
        } catch (Exception e) {
            LOGGER.error("Cannot get all cities in range : ", e);
            throw new CityException("ERROR :: cannot get all cities in range: " + e.getMessage());
        }
    }

    public int getTotalCount() {
        try {
            int totalCount = new CityRepository().getTotalCount();
            if (totalCount == 0) {
                LOGGER.error("Total cities count is 0");
                throw new CityException("ERROR :: total cities count is 0");
            }
            return totalCount;
        } catch (Exception e) {
            LOGGER.error("Cannot get all cities count : ", e);
            throw new CityException("ERROR :: cannot get all cities count: " + e.getMessage());
        }
    }
}
