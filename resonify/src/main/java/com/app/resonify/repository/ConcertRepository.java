package com.app.resonify.repository;

import com.app.resonify.model.Concert;
import com.app.resonify.model.Theater;
import com.app.resonify.model.record.ConcertListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, UUID> {
    List<Concert> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT c FROM Concert c LEFT JOIN FETCH c.artists")
    List<Concert> findAllWithArtists();

    boolean existsByNameAndDateAndTheater(String name, LocalDate date, Theater theater);

    @Query("""
        SELECT c.id as id,
               c.name as name,
               c.date as date,
               c.type as type,
               (SELECT cp.photo 
                FROM ConcertPhoto cp 
                WHERE cp.concert = c 
                ORDER BY cp.id ASC 
                LIMIT 1) as photo
        FROM Concert c
        WHERE c.theater = :theater
        ORDER BY c.date DESC
    """)
    List<ConcertListProjection> findByTheater(@Param("theater") Theater theater);
}
