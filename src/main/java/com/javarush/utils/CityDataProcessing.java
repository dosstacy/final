package com.javarush.utils;

import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.services.CityServices;
import com.javarush.services.CountryServices;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CityDataProcessing {
    private final SessionFactory sessionFactory;
    private final CityServices cityServices;
    private final CountryServices countryServices;


    public CityDataProcessing(SessionFactory sessionFactory, CityServices cityServices, CountryServices countryServices) {
        this.sessionFactory = sessionFactory;
        this.cityServices = cityServices;
        this.countryServices = countryServices;
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityServices.getById(id);
                Set<CountryLanguage> languages = city.getCountryId().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    public List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            List<Country> countries = countryServices.getAll();

            int totalCount = cityServices.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityServices.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}
