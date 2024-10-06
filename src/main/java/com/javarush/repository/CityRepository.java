package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class CityRepository implements CrudRepository<City, Integer> {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public City getById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class)
                    .setParameter("ID", id)
                    .uniqueResult();
        }
    }

    @Override
    public City save(City entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from City c where c.id = :ID", City.class)
                    .setParameter("ID", id)
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public List<City> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from City c", City.class).list();
        }
    }

    @Override
    public List<City> getItems(int offset, int limit) {
        try(Session session = sessionFactory.getCurrentSession()) {
            return session.createQuery("select c from City c", City.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        }
    }

    @Override
    public int getTotalCount() {
        try(Session session = sessionFactory.openSession()) {
            return Math.toIntExact(session.createQuery("select count(c) from City c", Long.class).uniqueResult());
        }
    }
}
