package com.app.resonify.controller;

import com.app.resonify.model.Concert;
import com.app.resonify.service.FrontendAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Concert API", description = "Manage concerts and map data")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class FrontendAppController {

    private final FrontendAppService frontendAppService;

    public FrontendAppController(FrontendAppService frontendAppService) {
        this.frontendAppService = frontendAppService;
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
}
