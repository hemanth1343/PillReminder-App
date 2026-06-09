package com.pillreminder.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineResponse {

    private String medicineName;

    private String genericName;

    private String uses;

    private String sideEffects;

    private String dosage;

    private String whoCanTake;

    private String whoShouldAvoid;

    private String interactions;

    private String warnings;

    private String manufacturer;

    private String storage;
}