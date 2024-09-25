package com.javarush.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "code_2")
    private String code2;
    @Column(name = "name")
    private String name;
    @Column(name = "continent")
    @Enumerated(EnumType.ORDINAL)
    private Continent continent;
    @Column(name = "region")
    private String region;
    @Column(name = "surface_area")
    private BigDecimal surfaceArea;
    @Column(name = "indep_year")
    private short indepYear;
    @Column(name = "population")
    private int population;
    @Column(name = "life_expectancy")
    private BigDecimal lifeExpectancy;
    @Column(name = "gnp")
    private BigDecimal gnp;
    @Column(name = "gnpo_id")
    private BigDecimal gnpoId;
    @Column(name = "local_name")
    private String localName;
    @Column(name = "government_form")
    private String governmentForm;
    @Column(name = "head_of_state")
    private String headOfState;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capital")
    private City capital;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> languages;
}
