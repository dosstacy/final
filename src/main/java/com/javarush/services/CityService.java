package com.javarush.services;

import com.javarush.domain.entity.City;
import com.javarush.domain.exceptions.CityException;
import com.javarush.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);

    public City getById(Integer id) {
        try {
            City city = new CityRepository().getById(id);
            if (city == null) {
                LOGGER.error("ERROR :: city with id {} not found", id);
                throw new CityException("ERROR :: city with id " + id + " not found");
            }
            return city;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get city with id {}", id);
            throw new CityException("ERROR :: cannot get city with id " + id + e);
        }
    }

    public City save(City entity) {
        try {
            City city = new CityRepository().save(entity);
            if (city == null) {
                LOGGER.error("ERROR :: no city with id {}", entity.getId());
                throw new CityException("ERROR :: no city with id " + entity.getId());
            }
            return city;
        }catch(Exception e) {
            LOGGER.error("ERROR :: cannot save city with id {}", entity.getId());
            throw new CityException("ERROR :: cannot save city with id " + entity.getId());
        }
    }

    public void delete(Integer id) {
        try{
            new CityRepository().delete(id);
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot delete city with id: {}", id);
            throw new CityException("ERROR :: cannot delete city with id: " + id);
        }
    }

    public List<City> getAll() {
        try {
            List<City> cities = new CityRepository().getAll();
            if (cities.isEmpty()) {
                LOGGER.error("ERROR :: no cities found");
                throw new CityException("ERROR :: no cities found");
            }
            return cities;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get all cities");
            throw new CityException("ERROR :: cannot get all cities");
        }
    }

    public List<City> getItems(int offset, int limit) {
        try{
            List<City> cities = new CityRepository().getItems(offset, limit);
            if (cities.isEmpty()) {
                LOGGER.error("ERROR :: no cities found in range");
                throw new CityException("ERROR :: no cities found in range");
            }
            return cities;
        }catch (Exception e){
            LOGGER.error("ERROR :: cannot get all cities in range");
            throw new CityException("ERROR :: cannot get all cities in range");
        }
    }

    public int getTotalCount() {
        try{
            int totalCount = new CityRepository().getTotalCount();
            if (totalCount == 0) {
                LOGGER.error("ERROR :: total cities count is 0");
                throw new CityException("ERROR :: total cities count is 0");
            }
            return totalCount;
        }catch(Exception e){
            LOGGER.error("ERROR :: cannot get all cities count");
            throw new CityException("ERROR :: cannot get all cities count");
        }
    }
}
