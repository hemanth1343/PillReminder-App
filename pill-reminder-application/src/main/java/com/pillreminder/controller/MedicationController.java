package com.pillreminder.controller;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Medications", description = "CRUD for medications and pill inventory")
@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

	private final MedicationService medicationService;

	@Operation(summary = "Add a new medication")
	@PostMapping
	public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody CreateMedicationRequest request) {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}

		MedicationResponse response = medicationService.create(userDetails.getUsername(), request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get all medications (optionally filter by active)")
	@GetMapping
	public ResponseEntity<List<MedicationResponse>> getAll(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "true") boolean activeOnly) {
		return ResponseEntity.ok(medicationService.getAll(userDetails.getUsername(), activeOnly));
	}

	@Operation(summary = "Get a medication by ID")
	@GetMapping("/{id}")
	public ResponseEntity<MedicationResponse> getById(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id) {
		return ResponseEntity.ok(medicationService.getById(userDetails.getUsername(), id));
	}

	@Operation(summary = "Update a medication")
	@PutMapping("/{id}")
	public ResponseEntity<MedicationResponse> update(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id, @RequestBody UpdateMedicationRequest request) {
		return ResponseEntity.ok(medicationService.update(userDetails.getUsername(), id, request));
	}


	@Operation(summary = "Soft-delete (deactivate) a medication")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
		medicationService.delete(userDetails.getUsername(), id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get medications that need a refill")
	@GetMapping("/needs-refill")
	public ResponseEntity<List<MedicationResponse>> needsRefill(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(medicationService.getMedicationsNeedingRefill(userDetails.getUsername()));


	}
}
