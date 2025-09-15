package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.model.form.ConcertForm;
import com.app.resonify.repository.ConcertRepository;
import com.app.resonify.repository.TheaterRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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

    @GetMapping("/edit/{id}")
    public String editConcertForm(@PathVariable UUID id, Model model) {
        Concert concert = concertRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        model.addAttribute("concert", concert);
        model.addAttribute("theaters", theaterRepo.findAll());
        model.addAttribute("pageTitle", "Edit Concert");
        model.addAttribute("contentTemplate", "edit-concert");
        model.addAttribute("activePage", "editConcert");
        return "layout";
    }

    @PostMapping("/{id}")
    public String updateConcert(@PathVariable UUID id, @ModelAttribute ConcertForm form) {
        Concert concert = concertRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));

        concert.setName(form.getName());
        concert.setDate(form.getDate());
        concert.setTicket(form.getTicket());

        if (form.getArtists() != null && !form.getArtists().isBlank()) {
            concert.setArtists(Arrays.stream(form.getArtists().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList());
        } else {
            concert.setArtists(new ArrayList<>());
        }

        // process photos
        concert.setPhotos(form.getPhotos());
        concertRepo.save(concert);

        return "redirect:/web/concerts/list";
    }
}
