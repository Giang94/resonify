package com.app.resonify.repository;

import com.app.resonify.model.City;
import com.app.resonify.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TheaterRepository extends JpaRepository<Theater, UUID> {
    List<Theater> findByNameContainingIgnoreCase(String keyword);

    boolean existsByNameAndCity(String name, City city);

    Optional<Theater> findByName(String theaterName);
}
