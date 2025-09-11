package com.app.resonify.controller;

import com.app.resonify.model.City;
import com.app.resonify.model.Country;
import com.app.resonify.repository.CityRepository;
import com.app.resonify.repository.CountryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LocationApiController {

    private final CountryRepository countryRepo;
    private final CityRepository cityRepo;

    public LocationApiController(CountryRepository countryRepo, CityRepository cityRepo) {
        this.countryRepo = countryRepo;
        this.cityRepo = cityRepo;
    }

    @GetMapping("/countries")
    public List<Country> getCountriesByContinent(@RequestParam String continent) {
        return countryRepo.findByContinentName(continent);
    }

    @GetMapping("/cities")
    public List<City> getCitiesByCountry(@RequestParam String country) {
        return cityRepo.findByCountryName(country);
    }
}
