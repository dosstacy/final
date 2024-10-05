package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryRepository implements CrudRepository<Country, Long> {
    private final SessionFactory sessionFactory = HibernateUtil.prepareRelationalDb();

    @Override
    public Country getById(Long id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from Country c join fetch c.languages where c.id = :ID", Country.class)
                    .setParameter("ID", id)
                    .getSingleResult();
        }
    }

    @Override
    public Country save(Country entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.createQuery("delete from Country c where c.id = :ID", Country.class)
                    .setParameter("ID", id)
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Country> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from Country c join fetch c.languages", Country.class).list();
        }
    }

    @Override
    public List<Country> getItems(int offset, int limit) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> countryList = session.createQuery("select c from Country c", Country.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            session.getTransaction().commit();
            return countryList;
        }
    }

    @Override
    public int getTotalCount() {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            int intExact = Math.toIntExact(session.createQuery("select count(c) from Country c", Long.class).uniqueResult());
            session.getTransaction().commit();
            return intExact;
        }
    }
}
