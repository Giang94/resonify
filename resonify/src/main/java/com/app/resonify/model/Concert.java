package com.app.resonify.model;

import com.app.resonify.model.enums.ConcertType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @ToString.Exclude
    private Theater theater;

    @ElementCollection
    @CollectionTable(name = "concert_artists", joinColumns = @JoinColumn(name = "concert_id"))
    @Column(name = "artist")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> artists = new ArrayList<>();

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ConcertPhoto> photos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ConcertType type;
}
