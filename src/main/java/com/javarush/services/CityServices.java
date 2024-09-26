package com.javarush.services;

import com.javarush.domain.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CityServices {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServices.class);

    public CityServices(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<City> getItems(int offset, int limit) {
        try {
            Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.list();
        } catch (Exception e) {
            LOGGER.error("Error fetching cities with offset {} and limit {}. Error message :: {}", offset, limit, e.getMessage());
            return new ArrayList<>();
        }
    }

    public int getTotalCount() {
        try {
            Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
            return Math.toIntExact(query.uniqueResult());
        } catch (Exception e) {
            LOGGER.error("Error fetching total city count. Error message :: {}", e.getMessage());
            return 0;
        }
    }

    public City getById(Integer id) {
        try {
            Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class);
            query.setParameter("ID", id);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error fetching city with ID {}. Error message :: {}", id, e.getMessage());
            return null;
        }
    }
    //cover by tests, loggers
}
