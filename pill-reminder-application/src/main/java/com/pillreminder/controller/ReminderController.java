package com.pillreminder.controller;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reminders", description = "View and manage reminder logs")
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

	private final ReminderService reminderService;

	@Operation(summary = "Get today's reminders")
	@GetMapping("/today")
	public ResponseEntity<List<ReminderLogResponse>> getToday(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(reminderService.getTodayReminders(userDetails.getUsername()));
	}

	@Operation(summary = "Get reminders for a date range")
	@GetMapping
	public ResponseEntity<List<ReminderLogResponse>> getByRange(@AuthenticationPrincipal UserDetails userDetails,
			@Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(reminderService.getByDateRange(userDetails.getUsername(), from, to));
	}

	@Operation(summary = "Get all pending reminders")
	@GetMapping("/pending")
	public ResponseEntity<List<ReminderLogResponse>> getPending(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(reminderService.getPendingReminders(userDetails.getUsername()));
	}

	@Operation(summary = "Get a specific reminder log by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ReminderLogResponse> getById(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id) {
		return ResponseEntity.ok(reminderService.getById(userDetails.getUsername(), id));
	}

	@Operation(summary = "Mark a reminder as TAKEN")
	@PostMapping("/{id}/take")
	public ResponseEntity<ReminderLogResponse> markTaken(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id) {
		return ResponseEntity.ok(reminderService.markTaken(userDetails.getUsername(), id));
	}

	@Operation(summary = "Mark a reminder as MISSED")
	@PostMapping("/{id}/miss")
	public ResponseEntity<ReminderLogResponse> markMissed(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id) {
		return ResponseEntity.ok(reminderService.markMissed(userDetails.getUsername(), id));
	}

	@Operation(summary = "Snooze a reminder by N minutes")
	@PostMapping("/{id}/snooze")
	public ResponseEntity<ReminderLogResponse> snooze(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id, @RequestParam(defaultValue = "10") int minutes) {
		return ResponseEntity.ok(reminderService.snooze(userDetails.getUsername(), id, minutes));
	}

	@Operation(summary = "Skip a reminder (with optional reason)")
	@PostMapping("/{id}/skip")
	public ResponseEntity<ReminderLogResponse> skip(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long id, @RequestParam(required = false) String reason) {
		return ResponseEntity.ok(reminderService.skip(userDetails.getUsername(), id, reason));
	}

	@Operation(summary = "Get adherence statistics for a date range")
	@GetMapping("/stats/adherence")
	public ResponseEntity<AdherenceStatsResponse> getAdherence(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ResponseEntity.ok(reminderService.getAdherenceStats(userDetails.getUsername(), from, to));
	}

	@GetMapping("/generate")
	public String generate() {

		reminderService.generateDailyReminders(LocalDate.now());

		return "Generated";
	}
}
