package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CityRepository implements CrudRepository<City, Integer> {
    private final SessionFactory sessionFactory = HibernateUtil.prepareRelationalDb();

    @Override
    public City getById(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            City city = session.createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class)
                    .setParameter("ID", id)
                    .uniqueResult();
            session.getTransaction().commit();
            return city;
        }
    }

    @Override
    public City save(City entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
            return entity;
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
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
            session.beginTransaction();
            List<City> cityList = session.createQuery("select c from City c", City.class).list();
            session.getTransaction().commit();
            return cityList;
        }
    }

    @Override
    public List<City> getItems(int offset, int limit) {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<City> cityList = session.createQuery("select c from City c", City.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            session.getTransaction().commit();
            return cityList;
        }
    }

    @Override
    public int getTotalCount() {
        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            int intExact = Math.toIntExact(session.createQuery("select count(c) from City c", Long.class).uniqueResult());
            session.getTransaction().commit();
            return intExact;
        }
    }
}
