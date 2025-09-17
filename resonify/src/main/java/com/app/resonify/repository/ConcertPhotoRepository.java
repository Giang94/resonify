package com.app.resonify.repository;

import com.app.resonify.model.ConcertPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConcertPhotoRepository extends JpaRepository<ConcertPhoto, UUID> {
}
