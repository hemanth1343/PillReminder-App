package com.pillreminder.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pillreminder.config.JwtService;
import com.pillreminder.dto.Dtos.AuthResponse;
import com.pillreminder.dto.Dtos.LoginRequest;
import com.pillreminder.dto.Dtos.RefreshTokenRequest;
import com.pillreminder.dto.Dtos.RegisterRequest;
import com.pillreminder.dto.Dtos.UserResponse;
import com.pillreminder.entity.RefreshToken;
import com.pillreminder.entity.User;
import com.pillreminder.enums.Role;
import com.pillreminder.exception.EmailAlreadyExistsException;
import com.pillreminder.exception.TokenRefreshException;
import com.pillreminder.repository.RefreshTokenRepository;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final RefreshTokenRepository refreshTokenRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;

	@Value("${app.jwt.refresh-expiration}")
	private long refreshExpiration;

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {

			throw new EmailAlreadyExistsException(request.getEmail());
		}

		User user = User.builder()

				.email(request.getEmail())

				.password(passwordEncoder.encode(request.getPassword()))

				.fullName(request.getFullName())

				.phone(request.getPhone())

				.timezone(request.getTimezone() != null ? request.getTimezone() : "UTC")

				.role(Role.ROLE_USER)

				.build();

		userRepository.save(user);

		Map<String, Object> claims = new HashMap<>();

		claims.put("role", user.getRole().name());

		String accessToken = jwtService.generateToken(claims, org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail()).password(user.getPassword()).authorities(user.getRole().name()).build());

		String refreshToken = createRefreshToken(user);

		return buildAuthResponse(accessToken, refreshToken, user);
	}

	@Override
	@Transactional
	public AuthResponse login(LoginRequest request) {

	    // FIND USER

	    User user = userRepository

	            .findByEmail(
	                    request.getEmail()
	            )

	            .orElseThrow(() ->

	                    new BadCredentialsException(
	                            "Invalid email or password"
	                    )
	            );

	    // BLOCKED USER CHECK

	    if(Boolean.TRUE.equals(
	            user.getBlocked()
	    )){

	        throw new RuntimeException(

	                "You have been blocked, please contact to : "

	                +

	                user.getBlockedBy()
	        );
	    }

	    // PASSWORD AUTHENTICATION

	    authenticationManager.authenticate(

	            new UsernamePasswordAuthenticationToken(

	                    request.getEmail(),

	                    request.getPassword()
	            )
	    );

	    // JWT CLAIMS

	    Map<String, Object> claims =
	            new HashMap<>();

	    claims.put(
	            "role",
	            user.getRole().name()
	    );

	    // ACCESS TOKEN

	    String accessToken =
	            jwtService.generateToken(

	                    claims,

	                    org.springframework.security.core.userdetails.User

	                            .withUsername(
	                                    user.getEmail()
	                            )

	                            .password(
	                                    user.getPassword()
	                            )

	                            .authorities(
	                                    user.getRole().name()
	                            )

	                            .build()
	            );

	    // REFRESH TOKEN

	    String refreshToken =
	            createRefreshToken(user);

	    return buildAuthResponse(

	            accessToken,

	            refreshToken,

	            user
	    );
	}

	@Override
	@Transactional
	public AuthResponse refreshToken(RefreshTokenRequest request) {

		RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
				.orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

		if (stored.getExpiryDate().isBefore(Instant.now())) {

			refreshTokenRepository.deleteByUser(stored.getUser());

			throw new TokenRefreshException("Refresh token expired – please login again");
		}

		User user = stored.getUser();

		Map<String, Object> claims = new HashMap<>();

		claims.put("role", user.getRole().name());

		String newAccess = jwtService.generateToken(claims, org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail()).password(user.getPassword()).authorities(user.getRole().name()).build());

		String newRefresh = createRefreshToken(user);

		return buildAuthResponse(newAccess, newRefresh, user);
	}

	@Override
	@Transactional
	public void logout(String email) {

		userRepository.findByEmail(email)

				.ifPresent(refreshTokenRepository::deleteByUser);
	}

	private String createRefreshToken(User user) {

		refreshTokenRepository.deleteByUser(user);

		RefreshToken rt = RefreshToken.builder()

				.user(user)

				.token(UUID.randomUUID().toString())

				.expiryDate(Instant.now().plusMillis(refreshExpiration))

				.build();

		return refreshTokenRepository.save(rt).getToken();
	}

	private AuthResponse buildAuthResponse(

			String accessToken,

			String refreshToken,

			User user) {

		return AuthResponse.builder()

				.accessToken(accessToken)

				.refreshToken(refreshToken)

				.tokenType("Bearer")

				.expiresIn(86400L)

				.user(toUserResponse(user))

				.build();
	}

	private UserResponse toUserResponse(User user) {

		return UserResponse.builder()

				.id(user.getId())

				.email(user.getEmail())

				.fullName(user.getFullName())

				.phone(user.getPhone())

				.role(user.getRole())

				.emailNotifications(user.isEmailNotifications())

				.pushNotifications(user.isPushNotifications())

				.timezone(user.getTimezone())

				.createdAt(user.getCreatedAt())

				.build();
	}
}