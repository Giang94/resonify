package com.app.resonify.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "artists")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @ManyToMany(mappedBy = "artists")
    @ToString.Exclude
    @JsonBackReference
    private Set<Concert> concerts = new HashSet<>();
}