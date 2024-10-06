package com.javarush;

import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;

import java.util.Set;
import java.util.stream.Collectors;

public class DataTransformer {
    public CityCountry countryTransformToCityCountry(Country country) {
        CityCountry res = new CityCountry();
        res.setAlternativeCountryCode(country.getCode2());
        res.setContinent(country.getContinent());
        res.setCountryCode(country.getCode());
        res.setCountryName(country.getName());
        res.setCountryPopulation(country.getPopulation());
        res.setCountryRegion(country.getRegion());
        res.setCountrySurfaceArea(country.getSurfaceArea());
        Set<CountryLanguage> countryLanguages = country.getLanguages();
        Set<Language> languages = countryLanguages.stream().map(cl -> {
            Language language = new Language();
            language.setLanguage(cl.getLanguage());
            language.setIsOfficial(cl.getIsOfficial());
            language.setPercentage(cl.getPercentage());
            return language;
        }).collect(Collectors.toSet());
        res.setLanguages(languages);
        return res;
    }

    public CityCountry cityTransformToCityCountry(City city) {
        CityCountry res = new CityCountry();
        res.setId(city.getId());
        res.setName(city.getName());
        res.setPopulation(city.getPopulation());
        res.setDistrict(city.getDistrict());
        return res;
    }

    public City cityCountryTransformToCity(CityCountry cityCountry) {
        City city = new City();
        city.setId(cityCountry.getId());
        city.setName(cityCountry.getName());
        city.setPopulation(cityCountry.getPopulation());
        city.setDistrict(cityCountry.getDistrict());
        return city;
    }

    public Country cityCountryToCountry(CityCountry cityCountry) {
        Country country = new Country();
        country.setCode2(cityCountry.getAlternativeCountryCode());
        country.setContinent(cityCountry.getContinent());
        country.setCode(cityCountry.getCountryCode());
        country.setName(cityCountry.getCountryName());
        country.setPopulation(cityCountry.getCountryPopulation());
        country.setRegion(cityCountry.getCountryRegion());
        country.setSurfaceArea(cityCountry.getCountrySurfaceArea());
        Set<Language> languages = cityCountry.getLanguages();
        Set<CountryLanguage> countryLanguages = languages.stream().map(cl -> {
            CountryLanguage countryLanguage = new CountryLanguage();
            countryLanguage.setLanguage(cl.getLanguage());
            countryLanguage.setIsOfficial(cl.getIsOfficial());
            countryLanguage.setPercentage(cl.getPercentage());
            return countryLanguage;
        }).collect(Collectors.toSet());
        country.setLanguages(countryLanguages);
        return country;
    }
}
