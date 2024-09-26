package com.javarush;

import com.javarush.config.DatabaseConfig;
import com.javarush.config.RedisConfig;
import com.javarush.domain.City;
import com.javarush.redis.CityCountry;
import com.javarush.services.CityCountryTransformerService;
import com.javarush.services.CityService;
import com.javarush.services.CountryService;
import com.javarush.services.RedisService;
import com.javarush.utils.CityDataProcessing;
import io.lettuce.core.RedisClient;
import org.hibernate.SessionFactory;

import java.util.List;

import static java.util.Objects.nonNull;

public class Main {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    public Main() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        sessionFactory = databaseConfig.prepareRelationalDb();
        RedisConfig redisConfig = new RedisConfig();
        redisClient = redisConfig.prepareRedisClient();
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
        Main main = new Main();
        RedisService redisService = new RedisService(main.redisClient);

        CountryService countryService = new CountryService(main.sessionFactory);
        CityService cityService = new CityService(main.sessionFactory, countryService);
        CityCountryTransformerService transformer = new CityCountryTransformerService();
        CityDataProcessing cityDataProcessing = new CityDataProcessing(main.sessionFactory, cityService, countryService);

        List<City> allCities = cityDataProcessing.fetchData();
        List<CityCountry> preparedData = transformer.transformData(allCities);
        redisService.pushToRedis(preparedData);
        main.sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        redisService.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        cityDataProcessing.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        main.shutdown();
    }

}
