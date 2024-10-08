package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.cache.RedisRepository;
import com.javarush.config.HibernateUtil;
import com.javarush.config.RedisConfig;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import com.javarush.repository.CountryRepository;
import com.javarush.services.CityService;
import com.javarush.services.CountryService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

public class Application {
    public final SessionFactory sessionFactory;
    public final RedisClient redisClient;
    public final CityService cityService;
    public final CountryService countryService;
    public final ObjectMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public Application() {
        sessionFactory = HibernateUtil.getSessionFactory();
        redisClient = RedisConfig.prepareRedisClient();
        cityService = new CityService(new RedisRepository(), new CityRepository());
        countryService = new CountryService(new RedisRepository(), new CountryRepository());
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
        int numOfQueries = 15;
        Application application = new Application();
        RedisRepository countryRedisRepository = new RedisRepository();
        RedisRepository cityRedisRepository = new RedisRepository();

        System.out.println("Querying Country by ID...");
        for (int i = 0; i < numOfQueries; i++) {
            application.countryService.getById(1);
            application.cityService.getById(2);
        }
    }

    private void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Couldn't push to redis : ", e);
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
                    LOGGER.error("Couldn't test redis data : ", e);
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
