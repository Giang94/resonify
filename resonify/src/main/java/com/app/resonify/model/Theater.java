package com.app.resonify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "theaters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String address;
    private double lat;
    private double lng;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String photo;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
}
