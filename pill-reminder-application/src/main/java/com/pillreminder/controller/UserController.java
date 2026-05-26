package com.pillreminder.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
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

	@Operation(summary = "Soft-delete / deactivate account")
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
		userService.deleteAccount(userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}
	
	  private final UserRepository
      userRepository;

private final JwtService
      jwtService;

// =========================
// GET PROFILE
// =========================

@GetMapping("/profile")

public ResponseEntity<?> getProfile(

        @RequestHeader("Authorization")
        String authHeader

){

    try{

        String token =
                authHeader.substring(7);

        String email =
                jwtService.extractUsername(
                        token
                );

        User user =
                userRepository
                .findByEmail(email)
                .orElseThrow();

        Map<String,Object> data =
                new HashMap<>();

        data.put(
                "fullName",
                user.getFullName()
        );

        data.put(
                "email",
                user.getEmail()
        );

        data.put(
                "phone",
                user.getPhone()
        );

        return ResponseEntity.ok(
                data
        );
    }

    catch(Exception e){

        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(
                    "Failed To Load Profile"
                );
    }
}

// =========================
// UPDATE PROFILE
// =========================

@PutMapping("/profile")

public ResponseEntity<?> updateProfile(

        @RequestHeader("Authorization")
        String authHeader,

        @RequestBody
        Map<String,String> request

){

    try{

        String token =
                authHeader.substring(7);

        String email =
                jwtService.extractUsername(
                        token
                );

        User user =
                userRepository
                .findByEmail(email)
                .orElseThrow();

        user.setFullName(
                request.get(
                        "fullName"
                )
        );

        user.setPhone(
                request.get(
                        "phone"
                )
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                "Profile Updated"
        );
    }

    catch(Exception e){

        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(
                    "Failed To Update Profile"
                );
    }
}
}
