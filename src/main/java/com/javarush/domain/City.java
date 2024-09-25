package com.javarush.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country countryId;
    @Column(name = "name")
    private String name;
    @Column(name = "district")
    private String district;
    @Column(name = "population")
    private int population;
}
