package com.app.resonify.controller;


import com.app.resonify.model.Theater;
import com.app.resonify.repository.CityRepository;
import com.app.resonify.repository.TheaterRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/web/theaters")
public class WebTheaterController {

    private final TheaterRepository theaterRepo;
    private final CityRepository cityRepo;

    public WebTheaterController(TheaterRepository theaterRepo, CityRepository cityRepo) {
        this.theaterRepo = theaterRepo;
        this.cityRepo = cityRepo;
    }

    @GetMapping("/list")
    public String listTheaters(Model model) {
        model.addAttribute("theaters", theaterRepo.findAll());
        model.addAttribute("cities", cityRepo.findAll());
        model.addAttribute("pageTitle", "Theaters List");
        model.addAttribute("contentTemplate", "list-theaters");
        model.addAttribute("activePage", "theaters");
        return "layout";
    }

    @GetMapping("/add")
    public String addTheaterForm(Model model) {
        model.addAttribute("theater", new Theater());
        model.addAttribute("cities", cityRepo.findAll());
        model.addAttribute("pageTitle", "Add Theater");
        model.addAttribute("contentTemplate", "add-theater");
        model.addAttribute("activePage", "addTheater");
        return "layout";
    }

    @PostMapping
    public String saveTheater(@ModelAttribute Theater theater) {
        theaterRepo.save(theater);
        return "redirect:/web/theaters/list";
    }

    @GetMapping("/{id}/edit")
    public String editTheaterForm(@PathVariable UUID id, Model model) {
        Theater theater = theaterRepo.findById(id).orElseThrow();
        model.addAttribute("theater", theater);
        model.addAttribute("cities", cityRepo.findAll());
        model.addAttribute("pageTitle", "Edit Theater");
        model.addAttribute("contentTemplate", "edit-theater");
        return "layout";
    }

    @PostMapping("/{id}")
    public String updateTheater(@PathVariable UUID id, @ModelAttribute Theater theater) {
        Theater existed = theaterRepo.findById(id).orElseThrow();
        existed.setName(theater.getName());
        existed.setAddress(theater.getAddress());
        existed.setLat(theater.getLat());
        existed.setLng(theater.getLng());
        if (theater.getCity() != null) {
            existed.setCity(theater.getCity());
        }
        theaterRepo.save(existed);
        return "redirect:/web/theaters/list";
    }

    @DeleteMapping("/{id}")
    public String deleteTheater(@PathVariable UUID id) {
        theaterRepo.deleteById(id);
        return "redirect:/web/theaters/list";
    }
}
