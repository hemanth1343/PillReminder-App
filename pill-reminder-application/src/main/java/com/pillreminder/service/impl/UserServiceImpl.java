package com.pillreminder.service.impl;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.User;
import com.pillreminder.exception.*;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserResponse getCurrentUser(String email) {
		return toResponse(findUser(email));
	}

	@Override
	@Transactional
	public UserResponse updateUser(String email, UpdateUserRequest request) {
		User user = findUser(email);

		if (request.getFullName() != null)
			user.setFullName(request.getFullName());
		if (request.getPhone() != null)
			user.setPhone(request.getPhone());
		if (request.getTimezone() != null)
			user.setTimezone(request.getTimezone());
		if (request.getEmailNotifications() != null)
			user.setEmailNotifications(request.getEmailNotifications());
		if (request.getPushNotifications() != null)
			user.setPushNotifications(request.getPushNotifications());

		return toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public void changePassword(String email, ChangePasswordRequest request) {
		User user = findUser(email);

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void deleteAccount(String email) {
		User user = findUser(email);
		user.setEnabled(false);
		userRepository.save(user);
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	UserResponse toResponse(User user) {
		return UserResponse.builder().id(user.getId()).email(user.getEmail()).fullName(user.getFullName())
				.phone(user.getPhone()).role(user.getRole()).emailNotifications(user.isEmailNotifications())
				.pushNotifications(user.isPushNotifications()).timezone(user.getTimezone())
				.createdAt(user.getCreatedAt()).build();
	}

	@Override
	public void changePasswordAfterOtp(String email, String newPassword) {

		User user = userRepository.findByEmail(email).orElseThrow();

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(user);
	}

	@Override
	public void resetPassword(String email, String newPassword) {

		User user = userRepository.findByEmail(email).orElseThrow();

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(user);
	}

}
