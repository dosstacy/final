package com.javarush.services;

import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CityServices {
    private final SessionFactory sessionFactory;
    private final CountryServices countryServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServices.class);

    public CityServices(SessionFactory sessionFactory, CountryServices countryServices) {
        this.sessionFactory = sessionFactory;
        this.countryServices = countryServices;
    }

    public List<City> getItems(int offset, int limit) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public int getTotalCount() {
        Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

    public City getById(Integer id) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class);
        query.setParameter("ID", id);
        return query.getSingleResult();
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = getById(id);
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

            int totalCount = getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
    //cover by tests, loggers
}
