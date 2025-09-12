package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/web/concerts")
public class WebConcertController {

    private final ConcertRepository concertRepo;
    private final TheaterRepository theaterRepo;

    public WebConcertController(ConcertRepository concertRepo, TheaterRepository theaterRepo) {
        this.concertRepo = concertRepo;
        this.theaterRepo = theaterRepo;
    }

    // List concerts
    @GetMapping("/list")
    public String listConcerts(Model model) {
        model.addAttribute("concerts", concertRepo.findAll());
        model.addAttribute("pageTitle", "Concerts List");
        model.addAttribute("contentTemplate", "list-concerts");
        model.addAttribute("activePage", "concerts");
        return "layout";
    }

    // Add concert form
    @GetMapping("/add")
    public String addConcertForm(Model model) {
        model.addAttribute("concert", new Concert());
        model.addAttribute("theaters", theaterRepo.findAll());
        model.addAttribute("pageTitle", "Add Concert");
        model.addAttribute("contentTemplate", "add-concert");
        model.addAttribute("activePage", "addConcert");
        return "layout";
    }

    // Submit concert
    @PostMapping
    public String saveConcert(@ModelAttribute Concert concert) {
        concertRepo.save(concert);
        return "redirect:/web/concerts/list";
    }

    @DeleteMapping("/{id}")
    public String deleteConcert(@PathVariable UUID id) {
        concertRepo.deleteById(id);
        return "redirect:/web/concerts/list";
    }
}
