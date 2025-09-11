package com.app.resonify.repository;

import com.app.resonify.model.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContinentRepository extends JpaRepository<Continent, UUID> {
    Optional<Continent> findByName(String name);
}