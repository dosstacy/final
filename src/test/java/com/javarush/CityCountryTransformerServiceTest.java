package com.javarush;

import com.javarush.domain.entity.City;
import com.javarush.domain.enums.Continent;
import com.javarush.domain.entity.Country;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import com.javarush.services.CityCountryTransformerService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CityCountryTransformerServiceTest {
    @Test
    public void testInitialisation() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");
        city.setPopulation(100000);
        city.setDistrict("Test District");

        Country country = new Country();
        country.setCode("TC");
        country.setCode2("TCC");
        country.setContinent(Continent.ASIA);
        country.setName("Test Country");
        country.setPopulation(5000000);
        country.setRegion("Test Region");
        country.setSurfaceArea(new BigDecimal("12345.67"));

        CountryLanguage countryLanguage = new CountryLanguage();
        countryLanguage.setLanguage("Test Language");
        countryLanguage.setIsOfficial(true);
        countryLanguage.setPercentage(new BigDecimal("99.9"));

        city.setCountryId(country);
        country.setLanguages(Set.of(countryLanguage));

        CityCountryTransformerService cityCountryService = new CityCountryTransformerService();
        List<CityCountry> result = cityCountryService.initialisation(List.of(city));

        assertNotNull(result);
        assertEquals(1, result.size());
        CityCountry cityCountry = result.get(0);
        assertEquals(1, cityCountry.getId());
        assertEquals("Test City", cityCountry.getName());
        assertEquals(100000, cityCountry.getPopulation());
        assertEquals("Test District", cityCountry.getDistrict());
        assertEquals("TCC", cityCountry.getAlternativeCountryCode());
        assertEquals(Continent.ASIA, cityCountry.getContinent());
        assertEquals("TC", cityCountry.getCountryCode());
        assertEquals("Test Country", cityCountry.getCountryName());
        assertEquals(5000000, cityCountry.getCountryPopulation());
        assertEquals("Test Region", cityCountry.getCountryRegion());
        assertEquals(new BigDecimal("12345.67"), cityCountry.getCountrySurfaceArea());
        assertNotNull(cityCountry.getLanguages());
        assertEquals(1, cityCountry.getLanguages().size());
        Language language = cityCountry.getLanguages().iterator().next();
        assertEquals("Test Language", language.getLanguage());
        assertTrue(language.getIsOfficial());
        assertEquals(new BigDecimal("99.9"), language.getPercentage());
    }

}