package com.pillreminder.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.config.JwtService;
import com.pillreminder.dto.ChangePasswordOtpRequest;
import com.pillreminder.dto.Dtos.ChangePasswordRequest;
import com.pillreminder.dto.Dtos.UpdateUserRequest;
import com.pillreminder.dto.Dtos.UserResponse;
import com.pillreminder.entity.User;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

	@PostMapping("/me/change-password-otp")
	public ResponseEntity<?> changePasswordOtp(

			@AuthenticationPrincipal UserDetails userDetails,

			@RequestBody ChangePasswordOtpRequest request) {

		userService.changePasswordAfterOtp(

				userDetails.getUsername(),

				request.getNewPassword());

		return ResponseEntity.ok("Password Changed");
	}

	@Operation(summary = "Soft-delete / deactivate account")
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
		userService.deleteAccount(userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

	private final UserRepository userRepository;

	private final JwtService jwtService;

// =========================
// GET PROFILE
// =========================

	@GetMapping("/profile")

	public ResponseEntity<?> getProfile(

			@RequestHeader("Authorization") String authHeader

	) {

		try {

			String token = authHeader.substring(7);

			String email = jwtService.extractUsername(token);

			User user = userRepository.findByEmail(email).orElseThrow();

			Map<String, Object> data = new HashMap<>();

			data.put("fullName", user.getFullName());

			data.put("email", user.getEmail());

			data.put("phone", user.getPhone());

			data.put("profileImage", user.getProfileImage());

			data.put("emailNotifications", user.isEmailNotifications());

			data.put("pushNotifications", user.isPushNotifications());

			return ResponseEntity.ok(data);
		}

		catch (Exception e) {

			e.printStackTrace();

			return ResponseEntity.badRequest().body("Failed");
		}
	}

// =========================
// UPDATE PROFILE
// =========================

	@PutMapping("/profile")

	public ResponseEntity<?> updateProfile(

			@RequestHeader("Authorization") String authHeader,

			@RequestBody Map<String, Object> request

	) {

		try {

			String token = authHeader.substring(7);

			String email = jwtService.extractUsername(token);

			User user = userRepository.findByEmail(email).orElseThrow();

			// FULL NAME

			Object fullName = request.get("fullName");

			if (fullName != null) {

				user.setFullName(fullName.toString());
			}

			// PHONE

			Object phone = request.get("phone");

			if (phone != null) {

				user.setPhone(phone.toString());
			}

			// PROFILE IMAGE

			Object profileImage = request.get("profileImage");

			if (profileImage != null) {

				String image = profileImage.toString();

				System.out.println("IMAGE RECEIVED LENGTH: " + image.length());

				user.setProfileImage(image);
			}

			userRepository.save(user);

			return ResponseEntity.ok("Profile Updated");
		}

		catch (Exception e) {

			e.printStackTrace();

			return ResponseEntity.badRequest().body("Failed");
		}
	}

	@PutMapping("/preferences")

	public ResponseEntity<?> updatePreferences(

			Authentication authentication,

			@RequestBody Map<String, Object> request) {

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow();

		// EMAIL NOTIFICATIONS

		Boolean emailNotifications = (Boolean) request.get("emailNotifications");

		// PUSH NOTIFICATIONS

		Boolean pushNotifications = (Boolean) request.get("pushNotifications");

		user.setEmailNotifications(emailNotifications);

		user.setPushNotifications(pushNotifications);

		userRepository.save(user);

		return ResponseEntity.ok("Preferences Updated");
	}

}
