package com.app.resonify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "continent_id")
    private Continent continent;

    public Country(String countryName, Continent continent) {
        this.name = countryName;
        this.continent = continent;
    }
}

