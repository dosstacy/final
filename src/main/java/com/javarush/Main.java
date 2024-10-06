package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.cache.RedisRepository;
import com.javarush.config.HibernateUtil;
import com.javarush.config.RedisConfig;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.services.CityService;
import com.javarush.services.CountryService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

public class Main {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final CityService cityService;
    private final CountryService countryService;
    private final ObjectMapper mapper;

    public Main() {
        sessionFactory = HibernateUtil.getSessionFactory();
        redisClient = RedisConfig.prepareRedisClient();
        cityService = new CityService();
        countryService = new CountryService();
        mapper = new ObjectMapper();
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    public static void main(String[] args) {
        RedisRepository countryRedisRepository = new RedisRepository();
        RedisRepository cityRedisRepository = new RedisRepository();

        CountryService countryService = new CountryService();
        CityService cityService = new CityService();

        System.out.println("Querying Country by ID...");
        for (int i = 0; i < 15; i++) {
            countryService.getById(1);
            cityService.getById(2);
        }
    }

    private void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace(System.out);
                }
            }

        }
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityService.getById(id);
                Set<CountryLanguage> languages = city.getCountryId().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    public List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            countryService.getAll();

            int totalCount = cityService.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityService.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}
