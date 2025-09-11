package com.app.resonify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "city",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "country_id"})
)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}