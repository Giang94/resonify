package com.app.resonify.repository;

import com.app.resonify.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    Optional<Artist> findByName(String artistName);

    Optional<Artist> findByNameIgnoreCase(String artistName);
}
