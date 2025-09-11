package com.app.resonify.controller;

import com.app.resonify.model.City;
import com.app.resonify.repository.CityRepository;
import com.app.resonify.repository.CountryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/cities")
public class WebCityController {

    private final CityRepository cityRepo;
    private final CountryRepository countryRepo;

    public WebCityController(CityRepository cityRepo, CountryRepository countryRepo) {
        this.cityRepo = cityRepo;
        this.countryRepo = countryRepo;
    }

    @GetMapping("/list")
    public String listCities(Model model) {
        model.addAttribute("cities", cityRepo.findAll());
        model.addAttribute("pageTitle", "Cities List");
        model.addAttribute("contentTemplate", "list-cities");
        model.addAttribute("activePage", "cities");
        return "layout";
    }

    @GetMapping("/add")
    public String addCityForm(Model model) {
        model.addAttribute("city", new City());
        model.addAttribute("countries", countryRepo.findAll());
        model.addAttribute("pageTitle", "Add City");
        model.addAttribute("contentTemplate", "add-city");
        model.addAttribute("activePage", "addCity");
        return "layout";
    }

    @PostMapping
    public String saveCity(@ModelAttribute City city) {
        cityRepo.save(city);
        return "redirect:/web/cities/list";
    }
}
