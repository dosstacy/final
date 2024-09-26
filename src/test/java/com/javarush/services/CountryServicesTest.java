package com.javarush.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.javarush.domain.Country;
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

class CountryServicesTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Country> query;

    @InjectMocks
    private CountryServices countryServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testGetAll() {
        Country country1 = new Country();
        Country country2 = new Country();
        List<Country> expectedCountries = Arrays.asList(country1, country2);

        when(session.createQuery("select c from Country c join fetch c.languages", Country.class)).thenReturn(query);
        when(query.list()).thenReturn(expectedCountries);

        List<Country> actualCountries = countryServices.getAll();

        assertEquals(expectedCountries, actualCountries);
        verify(sessionFactory).getCurrentSession();
        verify(session).createQuery("select c from Country c join fetch c.languages", Country.class);
        verify(query).list();
    }
}