package com.app.resonify.repository;

import com.app.resonify.model.City;
import com.app.resonify.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
    List<City> findByCountryName(String country);

    Optional<City> findByName(String milan);

    boolean existsByNameAndCountry(String cityName, Country country);
}