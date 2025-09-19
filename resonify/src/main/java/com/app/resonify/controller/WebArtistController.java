package com.app.resonify.controller;

import com.app.resonify.model.Artist;
import com.app.resonify.repository.ArtistRepository;
import com.app.resonify.utils.PhotoHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/web/artists")
public class WebArtistController {

    private final ArtistRepository artistRepository;

    public WebArtistController(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping("/add")
    public String showAddArtistForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "add-artist";
    }

    @PostMapping
    public String addArtist(@ModelAttribute Artist artist) throws IOException {
        artist.setPhoto(PhotoHelper.getPhotoAsBase64(artist.getPhoto()));
        artistRepository.save(artist);
        return "redirect:/web/artists";
    }
}
