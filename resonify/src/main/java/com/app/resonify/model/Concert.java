package com.app.resonify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "concerts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String ticket;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @ElementCollection
    @CollectionTable(name = "concert_artists", joinColumns = @JoinColumn(name = "concert_id"))
    @Column(name = "artist")
    private List<String> artists;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concert_photos", joinColumns = @JoinColumn(name = "concert_id"))
    @Column(name = "photo", columnDefinition = "TEXT")
    private List<String> photos;
}
