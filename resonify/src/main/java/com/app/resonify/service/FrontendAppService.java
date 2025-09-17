package com.app.resonify.service;

import com.app.resonify.model.Concert;
import com.app.resonify.model.ConcertPhoto;
import com.app.resonify.repository.ConcertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FrontendAppService {

    private final ConcertRepository concertRepository;

    public FrontendAppService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }

    public Map<String, Long> getStats() {
        long concertCount = concertRepository.count();

        // distinct theaters from concerts
        long theaterCount = concertRepository.findAll().stream()
                .map(c -> c.getTheater().getId())
                .distinct()
                .count();

        // distinct cities from those theaters
        long cityCount = concertRepository.findAll().stream()
                .map(c -> c.getTheater().getCity().getId())
                .distinct()
                .count();

        // distinct countries from those cities
        long countryCount = concertRepository.findAll().stream()
                .map(c -> c.getTheater().getCity().getCountry().getId())
                .distinct()
                .count();

        return Map.of(
                "concertCount", concertCount,
                "theaterCount", theaterCount,
                "cityCount", cityCount,
                "countryCount", countryCount
        );
    }

    public List<String> getPhotos() {
        return concertRepository.findAll().stream()
                .flatMap(concert -> concert.getPhotos().stream().map(ConcertPhoto::getPhoto))
                .toList();
    }
}
