package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.model.ConcertPhoto;
import com.app.resonify.model.Theater;
import com.app.resonify.model.record.*;
import com.app.resonify.repository.ConcertRepository;
import com.app.resonify.repository.TheaterRepository;
import com.app.resonify.service.FrontendAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Concert API", description = "Manage concerts and map data")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class FrontendAppController {

    private final FrontendAppService frontendAppService;

    public FrontendAppController(FrontendAppService frontendAppService, ConcertRepository concertRepository, TheaterRepository theaterRepository) {
        this.frontendAppService = frontendAppService;
        this.concertRepository = concertRepository;
        this.theaterRepository = theaterRepository;
    }

    @Operation(summary = "Get all concerts")
    @GetMapping("/concerts")
    public List<Concert> getAllConcerts() {
        return frontendAppService.getAllConcerts();
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return frontendAppService.getStats();
    }

    @GetMapping("/photos")
    public List<String> getPhotos() {
        return frontendAppService.getPhotos();
    }

    private final ConcertRepository concertRepository;
    private final TheaterRepository theaterRepository;

    // -------------------------
    // 1. Concert summary for map
    // -------------------------
    @GetMapping("/concerts/summary")
    public List<ConcertSummaryRecord> getConcertSummary() {
        return concertRepository.findAll().stream()
                .map(c -> new ConcertSummaryRecord(
                        c.getId(),
                        c.getName(),
                        c.getDate(),
                        c.getType().name(),
                        c.getTheater().getId(),
                        c.getTheater().getName(),
                        c.getTheater().getLat(),
                        c.getTheater().getLng(),
                        c.getTheater().getPhoto(),
                        c.getTheater().getCity().getId(),
                        c.getTheater().getCity().getName(),
                        c.getTheater().getCity().getCountry().getId(),
                        c.getTheater().getCity().getCountry().getName(),
                        c.getTheater().getCity().getCountry().getContinent().getId(),
                        c.getTheater().getCity().getCountry().getContinent().getName()
                ))
                .toList();
    }

    // -------------------------
    // 2. Concerts in a theater (1 photo)
    // -------------------------
    @GetMapping("/theaters/{theaterId}/concerts")
    public List<ConcertListRecord> getConcertsByTheater(@PathVariable UUID theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theater not found"));

        return concertRepository.findByTheater(theater).stream()
                .map(p -> new ConcertListRecord(
                        p.getId(),
                        p.getName(),
                        p.getDate(),
                        p.getType(),
                        p.getPhoto()
                ))
                .toList();
    }

    // -------------------------
    // 3. Full concert details
    // -------------------------
    @GetMapping("/concerts/{concertId}")
    public ConcertDetailsRecord getConcertDetails(@PathVariable UUID concertId) {
        Concert c = concertRepository.findById(concertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concert not found"));

        List<ArtistRecord> artists = c.getArtists().stream()
                .map(a -> new ArtistRecord(a.getId(), a.getName(), a.getPhoto()))
                .toList();

        List<String> photos = c.getPhotos().stream()
                .map(ConcertPhoto::getPhoto)
                .toList();

        TheaterRecord theater = new TheaterRecord(c.getTheater().getId(), c.getTheater().getName());

        return new ConcertDetailsRecord(
                c.getId(),
                c.getName(),
                c.getDate(),
                c.getTicket(),
                c.getType().name(),
                theater,
                artists,
                photos
        );
    }
}
