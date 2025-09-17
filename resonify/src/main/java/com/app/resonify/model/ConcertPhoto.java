package com.app.resonify.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "concert_photos")
public class ConcertPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @ManyToOne
    @JoinColumn(name = "concert_id")
    @ToString.Exclude
    @JsonBackReference
    private Concert concert;

    @Column(name = "photo_size")
    private Integer photoSize;
}