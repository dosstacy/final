package com.javarush.services;

import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.CountryException;
import com.javarush.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);

    public Country getById(Long id) {
        try {
            Country country = new CountryRepository().getById(id);
            if (country == null) {
                LOGGER.error("ERROR :: country with id {} not found", id);
                throw new CountryException("ERROR :: country with id " + id + " not found");
            }
            return country;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get country with id {}", id);
            throw new CountryException("ERROR :: cannot get country with id " + id);
        }
    }

    public Country save(Country entity) {
        try {
            Country country = new CountryRepository().save(entity);
            if (country == null) {
                LOGGER.error("ERROR :: no country with id {}", entity.getId());
                throw new CountryException("ERROR :: no country with id " + entity.getId());
            }
            return country;
        }catch(Exception e) {
            LOGGER.error("ERROR :: cannot save country with id {}", entity.getId());
            throw new CountryException("ERROR :: cannot save country with id " + entity.getId());
        }
    }

    public void delete(Long id) {
        try{
            new CountryRepository().delete(id);
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot delete country with id: {}", id);
            throw new CountryException("ERROR :: cannot delete country with id: " + id);
        }
    }

    public List<Country> getAll() {
        try {
            List<Country> countries = new CountryRepository().getAll();
            if (countries.isEmpty()) {
                LOGGER.error("ERROR :: no countries found");
                throw new CountryException("ERROR :: no countries found");
            }
            return countries;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get all countries");
            throw new CountryException("ERROR :: cannot get all countries");
        }
    }

    public List<Country> getItems(int offset, int limit) {
        try{
            List<Country> countries = new CountryRepository().getItems(offset, limit);
            if (countries.isEmpty()) {
                LOGGER.error("ERROR :: no countries found in range");
                throw new CountryException("ERROR :: no countries found in range");
            }
            return countries;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get all countries in range");
            throw new CountryException("ERROR :: cannot get all countries in range");
        }
    }

    public int getTotalCount() {
        try{
            int totalCount = new CountryRepository().getTotalCount();
            if (totalCount == 0) {
                LOGGER.error("ERROR :: total countries count is 0");
                throw new CountryException("ERROR :: total countries count is 0");
            }
            return totalCount;
        }catch(Exception e){
            LOGGER.error("ERROR :: cannot get all countries count");
            throw new CountryException("ERROR :: cannot get all countries count");
        }
    }
}
