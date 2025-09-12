package com.app.resonify.controller;

import com.app.resonify.repository.ConcertRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebHomeController {

    private final ConcertRepository concertRepo;

    public WebHomeController(ConcertRepository concertRepo) {
        this.concertRepo = concertRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        long concertCount = concertRepo.count();

        // distinct theaters from concerts
        long theaterCount = concertRepo.findAll().stream()
                .map(c -> c.getTheater().getId())
                .distinct()
                .count();

        // distinct cities from those theaters
        long cityCount = concertRepo.findAll().stream()
                .map(c -> c.getTheater().getCity().getId())
                .distinct()
                .count();

        // distinct countries from those cities
        long countryCount = concertRepo.findAll().stream()
                .map(c -> c.getTheater().getCity().getCountry().getId())
                .distinct()
                .count();

        model.addAttribute("concertCount", concertCount);
        model.addAttribute("theaterCount", theaterCount);
        model.addAttribute("cityCount", cityCount);
        model.addAttribute("countryCount", countryCount);
        model.addAttribute("contentTemplate", "home");
        return "layout";
    }
}
