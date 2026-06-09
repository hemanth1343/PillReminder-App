package com.pillreminder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineName;

    private String genericName;

    @Column(length = 5000)
    private String uses;

    @Column(length = 5000)
    private String sideEffects;

    @Column(length = 3000)
    private String dosage;

    @Column(length = 3000)
    private String whoCanTake;

    @Column(length = 3000)
    private String whoShouldAvoid;

    @Column(length = 3000)
    private String interactions;

    @Column(length = 3000)
    private String warnings;

    private String manufacturer;

    private String storage;

    private LocalDateTime lastUpdated;
}