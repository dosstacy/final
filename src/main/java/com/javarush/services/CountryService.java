package com.javarush.services;

import com.javarush.DataTransformer;
import com.javarush.cache.RedisRepository;
import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.CountryException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);
    private final RedisRepository redisRepository = new RedisRepository();
    private final CountryRepository countryRepository = new CountryRepository();
    private final DataTransformer dataTransformer = new DataTransformer();

    public Country getById(Integer id) {
        String key = "country_" + id;
        try {
            if (redisRepository.checkExists(key)) {
                CityCountry cityCountry = redisRepository.getById(key, CityCountry.class);
                return dataTransformer.cityCountryToCountry(cityCountry);
            }
            Country country = countryRepository.getById(id);
            redisRepository.put(key, dataTransformer.countryTransformToCityCountry(country));
            return country;
        } catch (Exception e) {
            LOGGER.error("Cannot get country with id {}", id);
            throw new CountryException("ERROR :: cannot get country with id " + id + " " + e.getMessage());
        }
    }

    public Country save(Country entity) {
        try {
            Country savedCountry = countryRepository.save(entity);
            if (savedCountry == null) {
                LOGGER.error("Failed to save country with id {}", entity.getId());
                throw new CountryException("ERROR :: no country with id " + entity.getId());
            }
            return savedCountry;
        } catch (Exception ex) {
            LOGGER.error("Could not save country with id {}", entity.getId(), ex);
            throw new CountryException("ERROR :: could not save country with id " + entity.getId() + " " + ex.getMessage());
        }
    }

    public void delete(Integer id) {
        try {
            countryRepository.delete(id);
        } catch (Exception e) {
            LOGGER.error("Cannot delete country with id: {}", id);
            throw new CountryException("ERROR :: cannot delete country with id: " + id + " " + e.getMessage());
        }
    }

    public List<Country> getAll() {
        try {
            List<Country> countries = countryRepository.getAll();
            if (countries.isEmpty()) {
                LOGGER.error("No countries found");
                throw new CountryException("ERROR :: no countries found");
            }
            return countries;
        } catch (Exception e) {
            LOGGER.error("Cannot get all countries");
            throw new CountryException("ERROR :: cannot get all countries: " + e.getMessage());
        }
    }

    public List<Country> getItems(int offset, int limit) {
        try {
            List<Country> countries = countryRepository.getItems(offset, limit);
            if (countries.isEmpty()) {
                LOGGER.error("No countries found in range");
                throw new CountryException("ERROR :: no countries found in range");
            }
            return countries;
        } catch (Exception e) {
            LOGGER.error("Cannot get all countries in range");
            throw new CountryException("ERROR :: cannot get all countries in range: " + e.getMessage());
        }
    }

    public int getTotalCount() {
        try {
            int totalCount = countryRepository.getTotalCount();
            if (totalCount == 0) {
                LOGGER.error("Total countries count is 0");
                throw new CountryException("ERROR :: total countries count is 0");
            }
            return totalCount;
        } catch (Exception e) {
            LOGGER.error("Cannot get all countries count");
            throw new CountryException("ERROR :: cannot get all countries count: " + e.getMessage());
        }
    }
}
