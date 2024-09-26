package com.javarush.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.javarush.domain.City;
import com.javarush.domain.Country;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class CityServicesTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private Query<City> cityQuery;

    @Mock
    private Query<Long> countQuery;

    @Mock
    private CountryServices countryServices;

    @InjectMocks
    private CityServices cityServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(transaction);
    }

    @Test
    public void testGetItems() {
        City city1 = new City();
        City city2 = new City();
        List<City> expectedCities = Arrays.asList(city1, city2);

        when(session.createQuery("select c from City c", City.class)).thenReturn(cityQuery);
        when(cityQuery.setFirstResult(anyInt())).thenReturn(cityQuery);
        when(cityQuery.setMaxResults(anyInt())).thenReturn(cityQuery);
        when(cityQuery.list()).thenReturn(expectedCities);

        List<City> actualCities = cityServices.getItems(0, 10);

        assertEquals(expectedCities, actualCities);
        verify(session).createQuery("select c from City c", City.class);
        verify(cityQuery).setFirstResult(0);
        verify(cityQuery).setMaxResults(10);
        verify(cityQuery).list();
    }

    @Test
    public void testGetTotalCount() {
        when(session.createQuery("select count(c) from City c", Long.class)).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(100L);

        int totalCount = cityServices.getTotalCount();

        assertEquals(100, totalCount);
        verify(session).createQuery("select count(c) from City c", Long.class);
        verify(countQuery).uniqueResult();
    }

    @Test
    public void testGetById() {
        City expectedCity = new City();
        when(session.createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class)).thenReturn(cityQuery);
        when(cityQuery.setParameter("ID", 1)).thenReturn(cityQuery);
        when(cityQuery.getSingleResult()).thenReturn(expectedCity);

        City actualCity = cityServices.getById(1);

        assertEquals(expectedCity, actualCity);
        verify(session).createQuery("select c from City c join fetch c.countryId where c.id = :ID", City.class);
        verify(cityQuery).setParameter("ID", 1);
        verify(cityQuery).getSingleResult();
    }
}