package com.pillreminder.controller;

import com.pillreminder.dto.MedicineResponse;
import com.pillreminder.service.MedicineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Medicine Search",
    description = "Search real-time medicine information"
)
@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicineController {

    private final MedicineService medicineService;

    @Operation(
        summary = "Search medicine information by medicine name"
    )
    @GetMapping("/{medicineName}")
    public ResponseEntity<?> getMedicine(

            @PathVariable
            String medicineName
    ) {

        try {

            MedicineResponse response =

                    medicineService.getMedicine(
                            medicineName
                    );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );
        }
    }
}