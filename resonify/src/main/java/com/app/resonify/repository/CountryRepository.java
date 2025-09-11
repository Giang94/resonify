package com.app.resonify.repository;

import com.app.resonify.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID> {
    Optional<Country> findByName(String countryName);

    List<Country> findByContinentName(String continent);
}