package com.pillreminder.controller;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Current user profile and settings")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "Get current user profile")
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
	}

	@Operation(summary = "Update current user profile")
	@PutMapping("/me")
	public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody UpdateUserRequest request) {
		return ResponseEntity.ok(userService.updateUser(userDetails.getUsername(), request));
	}

	@Operation(summary = "Change password")
	@PostMapping("/me/change-password")
	public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(userDetails.getUsername(), request);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Soft-delete / deactivate account")
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
		userService.deleteAccount(userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}
}
