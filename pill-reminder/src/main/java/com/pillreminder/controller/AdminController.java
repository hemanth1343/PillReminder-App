package com.pillreminder.controller;

import com.pillreminder.entity.Medication;
import com.pillreminder.entity.ReminderLog;
import com.pillreminder.entity.User;

import com.pillreminder.enums.Role;

import com.pillreminder.repository.MedicationRepository;
import com.pillreminder.repository.ReminderLogRepository;
import com.pillreminder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserRepository userRepository;

	private final MedicationRepository medicationRepository;

	private final ReminderLogRepository reminderLogRepository;

	private final PasswordEncoder passwordEncoder;

	@GetMapping("/stats/summary")
	public Map<String, Object> getSummary() {

		Map<String, Object> summary = new HashMap<>();

		List<User> users = userRepository.findAll();

		List<Medication> medications = medicationRepository.findAll();

		List<ReminderLog> logs = reminderLogRepository.findAll();

		summary.put("totalUsers", users.size());

		summary.put("totalMedications", medications.size());

		summary.put("totalReminderLogs", logs.size());

		return summary;
	}

	@GetMapping("/users")
	public List<User> getUsers() {

		return userRepository.findAll();
	}

	@GetMapping("/medications")
	public List<Medication> getMedications() {

		return medicationRepository.findAll();
	}

	@GetMapping("/reminders")
	public List<ReminderLog> getReminders() {

		return reminderLogRepository.findAll();
	}

	@PostMapping("/create-admin")
	public ResponseEntity<?> createAdmin(@RequestBody User request) {

		boolean exists = userRepository.existsByEmail(request.getEmail());

		if (exists) {

			return ResponseEntity.badRequest().body("Email already exists");
		}

		User admin = new User();

		admin.setFullName(request.getFullName());

		admin.setEmail(request.getEmail());

		admin.setPassword(passwordEncoder.encode(request.getPassword()));

		admin.setPhone(request.getPhone());

		admin.setTimezone(request.getTimezone());

		admin.setRole(Role.ROLE_ADMIN);

		userRepository.save(admin);

		return ResponseEntity.ok("Admin created successfully");
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {

		userRepository.deleteById(id);

		return ResponseEntity.ok("User deleted successfully");
	}

	@DeleteMapping("/medications/{id}")
	public ResponseEntity<?> deleteMedication(@PathVariable Long id) {

		medicationRepository.deleteById(id);

		return ResponseEntity.ok("Medication deleted successfully");
	}

	@DeleteMapping("/reminders/{id}")
	public ResponseEntity<?> deleteReminder(@PathVariable Long id) {

		reminderLogRepository.deleteById(id);

		return ResponseEntity.ok("Reminder deleted successfully");
	}
}