package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Concert API", description = "Manage concerts and map data")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @Operation(summary = "Get all concerts")
    @GetMapping
    public List<Concert> getAllConcerts() {
        return concertService.getAllConcerts();
    }
}
