package com.app.resonify.repository;

import com.app.resonify.model.Concert;
import com.app.resonify.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, UUID> {
    List<Concert> findByNameContainingIgnoreCase(String keyword);

    boolean existsByNameAndDateAndTheater(String name, LocalDate date, Theater theater);
}
