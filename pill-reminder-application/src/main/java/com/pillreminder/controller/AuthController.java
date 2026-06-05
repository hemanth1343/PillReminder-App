package com.pillreminder.controller;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.dto.ChangePasswordOtpRequest;
import com.pillreminder.dto.Dtos.AuthResponse;
import com.pillreminder.dto.Dtos.LoginRequest;
import com.pillreminder.dto.Dtos.RefreshTokenRequest;
import com.pillreminder.dto.Dtos.RegisterRequest;
import com.pillreminder.entity.EmailOtp;
import com.pillreminder.entity.User;
import com.pillreminder.repository.EmailOtpRepository;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.service.AuthService;
import com.pillreminder.service.MailService;
import com.pillreminder.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "Register, login, token refresh and logout")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	
	private final EmailOtpRepository otpRepository;

	private final MailService mailService;
	
	private final UserService userService;
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

	@Operation(summary = "Register a new user")
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@Operation(summary = "Login and receive JWT tokens")
	@PostMapping("/login")
	public ResponseEntity<?> login(
	        @Valid @RequestBody LoginRequest request
	) {

	    try{

	        return ResponseEntity.ok(
	                authService.login(request)
	        );
	    }

	    catch(Exception e){

	        return ResponseEntity
	                .status(401)
	                .body(
	                        e.getMessage()
	                );
	    }
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
	
	 @PostMapping("/send-otp")
	    public ResponseEntity<?> sendOtp(

	            @RequestParam String email
	    ) {

	        String otp =
	                String.valueOf(
	                        100000 +
	                        new Random()
	                                .nextInt(900000)
	                );

	        EmailOtp emailOtp =
	                EmailOtp.builder()

	                        .email(email)

	                        .otp(otp)

	                        .expiryTime(
	                                LocalDateTime.now()
	                                        .plusMinutes(5)
	                        )

	                        .build();

	       
			otpRepository.deleteByEmail(email);

	        otpRepository.save(emailOtp);

			mailService.sendOtpMail(
	                email,
	                otp
	        );

	        return ResponseEntity.ok(
	                "OTP Sent"
	        );
	    }
	    
	    @PostMapping("/verify-otp")
	    public ResponseEntity<?> verifyOtp(

	            @RequestParam String email,

	            @RequestParam String otp
	    ) {

	        EmailOtp stored =
	                otpRepository
	                        .findByEmail(email)
	                        .orElseThrow();

	        if(
	                stored.getExpiryTime()
	                        .isBefore(
	                                LocalDateTime.now()
	                        )
	        ){

	            return ResponseEntity.badRequest()
	                    .body("OTP Expired");
	        }

	        if(
	                !stored.getOtp()
	                        .equals(otp)
	        ){

	            return ResponseEntity.badRequest()
	                    .body("Invalid OTP");
	        }

	        otpRepository.delete(stored);

	        return ResponseEntity.ok(
	                "OTP Verified"
	        );
	    }
	    
	    @PostMapping("/send-password-otp")
	    public ResponseEntity<?> sendPasswordOtp(

	            @AuthenticationPrincipal
	            UserDetails userDetails
	    ) {

	        User user =
	                userRepository
	                .findByEmail(
	                        userDetails.getUsername()
	                )
	                .orElseThrow();

	        String otp =
	                String.valueOf(
	                        100000 +
	                        new Random().nextInt(900000)
	                );

	        EmailOtp emailOtp =
	                EmailOtp.builder()

	                        .email(user.getEmail())

	                        .otp(otp)

	                        .expiryTime(
	                                LocalDateTime.now()
	                                        .plusMinutes(5)
	                        )

	                        .build();

	        otpRepository
	                .findByEmail(user.getEmail())
	                .ifPresent(
	                        otpRepository::delete
	                );

	        otpRepository.save(emailOtp);

	        mailService.sendOtpMail(
	                user.getEmail(),
	                otp
	        );

	        return ResponseEntity.ok(
	                "OTP Sent"
	        );
	    }
	    
	    @PostMapping("/verify-password-otp")
	    public ResponseEntity<?> verifyPasswordOtp(

	            @AuthenticationPrincipal
	            UserDetails userDetails,

	            @RequestParam String otp
	    ) {

	        User user =
	                userRepository
	                .findByEmail(
	                        userDetails.getUsername()
	                )
	                .orElseThrow();

	        EmailOtp stored =
	                otpRepository
	                        .findByEmail(
	                                user.getEmail()
	                        )
	                        .orElseThrow();

	        if(

	            stored.getExpiryTime()
	                  .isBefore(
	                          LocalDateTime.now()
	                  )

	        ){

	            return ResponseEntity
	                    .badRequest()
	                    .body(
	                            "OTP Expired"
	                    );
	        }

	        if(

	            !stored.getOtp()
	                   .equals(otp)

	        ){

	            return ResponseEntity
	                    .badRequest()
	                    .body(
	                            "Invalid OTP"
	                    );
	        }

	        otpRepository.delete(stored);

	        return ResponseEntity.ok(
	                "OTP Verified"
	        );
	    }
	    
	    @PostMapping("/me/change-password")
	    public ResponseEntity<?> changePassword(

	            @AuthenticationPrincipal
	            UserDetails userDetails,

	            @RequestBody
	            ChangePasswordOtpRequest request
	    ) {

	        userService.changePasswordAfterOtp(

	                userDetails.getUsername(),

	                request.getNewPassword()
	        );

	        return ResponseEntity.ok(
	                "Password Changed"
	        );
	    }
	    
	    @PostMapping("/forgot-password/send-otp")
	    public ResponseEntity<?> sendForgotPasswordOtp(

	            @RequestParam String email
	    ) {

	        User user =
	                userRepository
	                        .findByEmail(email)
	                        .orElse(null);

	        if(user == null){

	            return ResponseEntity
	                    .badRequest()
	                    .body("Email Not Registered");
	        }

	        String otp =
	                String.valueOf(
	                        100000 +
	                        new Random().nextInt(900000)
	                );

	        EmailOtp emailOtp =
	                EmailOtp.builder()

	                        .email(email)

	                        .otp(otp)

	                        .expiryTime(
	                                LocalDateTime.now()
	                                        .plusMinutes(5)
	                        )

	                        .build();

	        otpRepository
	                .findByEmail(email)
	                .ifPresent(
	                        otpRepository::delete
	                );

	        otpRepository.save(emailOtp);

	        mailService.sendOtpMail(
	                email,
	                otp
	        );

	        return ResponseEntity.ok(
	                "OTP Sent"
	        );
	    }
	    
	    @PostMapping("/forgot-password/verify-otp")
	    public ResponseEntity<?> verifyForgotOtp(
  
	            @RequestParam String email,

	            @RequestParam String otp
	    ) {

	        EmailOtp stored =
	                otpRepository
	                        .findByEmail(email)
	                        .orElseThrow();

	        if(

	            stored.getExpiryTime()
	                    .isBefore(
	                            LocalDateTime.now()
	                    )

	        ){

	            return ResponseEntity
	                    .badRequest()
	                    .body(
	                            "OTP Expired"
	                    );
	        }

	        if(

	            !stored.getOtp()
	                    .equals(otp)

	        ){

	            return ResponseEntity
	                    .badRequest()
	                    .body(
	                            "Invalid OTP"
	                    );
	        }

	        return ResponseEntity.ok(
	                "OTP Verified"
	        );
	    }
	    
	    @PostMapping("/forgot-password/reset")
	    public ResponseEntity<?> resetPassword(

	            @RequestParam String email,

	            @RequestParam String newPassword
	    ) {

	        User user =
	                userRepository
	                        .findByEmail(email)
	                        .orElseThrow();

	        user.setPassword(

	                passwordEncoder.encode(
	                        newPassword
	                )
	        );

	        userRepository.save(user);

	        otpRepository
	                .findByEmail(email)
	                .ifPresent(
	                        otpRepository::delete
	                );

	        return ResponseEntity.ok(
	                "Password Reset Successful"
	        );
	    }
	    
}
