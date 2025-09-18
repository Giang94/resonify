package com.app.resonify.service;

import com.app.resonify.model.Artist;
import com.app.resonify.model.Concert;
import com.app.resonify.repository.ArtistRepository;
import com.app.resonify.repository.ConcertRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Log4j2
public class ArtistImportService {
    private final ObjectMapper objectMapper;
    private final ArtistRepository artistRepository;
    private final ConcertRepository concertRepository;

    public ArtistImportService(ObjectMapper objectMapper,
                               ArtistRepository artistRepository,
                               ConcertRepository concertRepository) {
        this.objectMapper = objectMapper;
        this.artistRepository = artistRepository;
        this.concertRepository = concertRepository;
    }

    @Transactional
    public void importFromFile(String path) throws Exception {
        File file = new File(path);
        JsonNode root = objectMapper.readTree(file);

        for (JsonNode node : root.get("concert_artists")) {
            UUID concertId = UUID.fromString(node.get("concert_id").asText());
            String artistName = node.get("artist").asText().trim();

            Concert concert = concertRepository.findById(concertId)
                    .orElseThrow(() -> new RuntimeException("Concert not found: " + concertId));

            Artist artist = artistRepository.findByName(artistName)
                    .orElseGet(() -> {
                        Artist a = new Artist();
                        a.setName(artistName);
                        log.info("Created new artist '{}'", artistName);
                        return artistRepository.save(a);
                    });

            if (!concert.getArtists().contains(artist)) {
                concert.getArtists().add(artist);
                // no need to explicitly save, since transaction will flush
            }
            log.info("Linked artist '{}' to concert '{}'", artistName, concert.getName());
        }
    }
}

