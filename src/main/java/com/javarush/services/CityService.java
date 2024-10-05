package com.javarush.services;

import com.javarush.domain.entity.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CityService {
    private final SessionFactory sessionFactory;
    private final CountryService countryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);

    public CityService(SessionFactory sessionFactory, CountryService countryService) {
        this.sessionFactory = sessionFactory;
        this.countryService = countryService;
    }

    public List<City> getItems(int offset, int limit) {
        try {
            System.out.println("HELLO from getItems");
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
            System.out.println("HELLO from getTotal count");
            Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
            return Math.toIntExact(query.uniqueResult());
        } catch (Exception e) {
            LOGGER.error("Error fetching total city count. Error message :: {}", e.getMessage());
            return 0;
        }
    }

    public City getById(Integer id) {
        try {
            System.out.println("HELLO from get by id");
            Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class);
            query.setParameter("ID", id);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error fetching city with ID {}. Error message :: {}", id, e.getMessage());
            return null;
        }
    }
}
