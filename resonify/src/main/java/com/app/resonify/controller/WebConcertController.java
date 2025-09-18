package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.model.enums.ConcertType;
import com.app.resonify.model.form.ConcertForm;
import com.app.resonify.repository.ArtistRepository;
import com.app.resonify.repository.ConcertRepository;
import com.app.resonify.repository.TheaterRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/web/concerts")
public class WebConcertController {

    private final String LAYOUT = "layout";
    private final ConcertRepository concertRepo;
    private final TheaterRepository theaterRepo;
    private final ArtistRepository artistRepo;

    public WebConcertController(ConcertRepository concertRepo, TheaterRepository theaterRepo, ArtistRepository artistRepo) {
        this.concertRepo = concertRepo;
        this.theaterRepo = theaterRepo;
        this.artistRepo = artistRepo;
    }

    // List concerts
    @GetMapping("/list")
    public String listConcerts(Model model) {
        List<Concert> concerts = concertRepo.findAll()
                .stream().map(c -> {
                    c.setPhotos(null);
                    return c;
                }).toList();
        model.addAttribute("concerts", concerts);
        model.addAttribute("types", ConcertType.values());
        model.addAttribute("pageTitle", "Concerts List");
        model.addAttribute("contentTemplate", "list-concerts");
        model.addAttribute("activePage", "concerts");
        return LAYOUT;
    }

    // Add concert form
    @GetMapping("/add")
    public String addConcertForm(Model model) {
        model.addAttribute("concert", new ConcertForm());
        model.addAttribute("theaters", theaterRepo.findAll());
        model.addAttribute("types", ConcertType.values());
        model.addAttribute("pageTitle", "Add Concert");
        model.addAttribute("contentTemplate", "add-concert");
        model.addAttribute("activePage", "addConcert");
        return LAYOUT;
    }

    // Submit concert
    @PostMapping
    public String saveConcert(@ModelAttribute ConcertForm form) throws IOException {
        Concert concert = new Concert();
        form.updateEntity(concert, theaterRepo, artistRepo);

        concertRepo.save(concert);
        return "redirect:/web/concerts/add";
    }

    @DeleteMapping("/{id}")
    public String deleteConcert(@PathVariable UUID id) {
        concertRepo.deleteById(id);
        return "redirect:/web/concerts/list";
    }

    @GetMapping("/edit/{id}")
    public String editConcertForm(@PathVariable UUID id, Model model) {
        Concert concert = concertRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        model.addAttribute("concert", ConcertForm.fromEntity(concert));
        model.addAttribute("theaters", theaterRepo.findAll());
        model.addAttribute("types", ConcertType.values());
        model.addAttribute("pageTitle", "Edit Concert");
        model.addAttribute("contentTemplate", "edit-concert");
        model.addAttribute("activePage", "editConcert");
        return LAYOUT;
    }

    @PostMapping("/{id}")
    public String updateConcert(@PathVariable UUID id, @ModelAttribute("concert") ConcertForm form) throws IOException {
        Concert concert = concertRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));

        form.updateEntity(concert, theaterRepo, artistRepo);
        concertRepo.save(concert);

        return "redirect:/web/concerts/list";
    }
}
