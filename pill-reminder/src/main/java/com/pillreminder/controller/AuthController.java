package com.pillreminder.controller;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.service.AuthService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Register, login, token refresh and logout")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Register a new user")
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@Operation(summary = "Login and receive JWT tokens")
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@Operation(summary = "Refresh access token using refresh token")
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(authService.refreshToken(request));
	}

	@Operation(summary = "Logout current user (invalidates refresh token)")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
		authService.logout(userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}
}
