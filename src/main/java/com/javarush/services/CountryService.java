package com.javarush.services;

import com.javarush.domain.entity.Country;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class CountryService {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);

    public CountryService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        try {
            Query<Country> query = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class);
            return query.list();
        } catch (Exception e) {
            LOGGER.error("Error fetching countries", e);
            return Collections.emptyList();
        }
    }
}
