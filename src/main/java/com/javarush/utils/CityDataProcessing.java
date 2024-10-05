package com.javarush.utils;

import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.services.CityService;
import com.javarush.services.CountryService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CityDataProcessing {
    private final SessionFactory sessionFactory;
    private final CityService cityService;
    private final CountryService countryService;


    public CityDataProcessing(SessionFactory sessionFactory, CityService cityService, CountryService countryService) {
        this.sessionFactory = sessionFactory;
        this.cityService = cityService;
        this.countryService = countryService;
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityService.getById(id);
                Set<CountryLanguage> languages = city.getCountryId().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    public List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            List<Country> countries = countryService.getAll();

            int totalCount = cityService.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityService.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}
