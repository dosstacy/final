package com.javarush.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
