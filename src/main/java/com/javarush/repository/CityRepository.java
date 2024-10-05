package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import com.javarush.services.CountryService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CityRepository implements CrudRepository<City, Long>{
    private final SessionFactory sessionFactory = HibernateUtil.prepareRelationalDb();
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);
    @Override
    public City getById(Long id) {
        try(Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from City c where c.id = :ID", City.class)
                    .setParameter("ID", id)
                    .getSingleResult();
        }
    }

    @Override
    public City save(City entity) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public void delete(Long id) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createQuery("delete from City c where c.id = :ID", City.class)
                .setParameter("ID", id)
                .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public List<City> getAll() {
        try(Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from City c", City.class).list();
        }
    }
}
