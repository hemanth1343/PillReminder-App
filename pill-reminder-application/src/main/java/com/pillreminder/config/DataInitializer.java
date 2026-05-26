package com.pillreminder.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pillreminder.entity.User;
import com.pillreminder.enums.Role;
import com.pillreminder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {

		boolean exists = userRepository.existsByEmail("superadmin@gmail.com");

		if (!exists) {

			User superAdmin = new User();

			superAdmin.setFullName("Main Admin");

			superAdmin.setEmail("superadmin@gmail.com");

			superAdmin.setPassword(passwordEncoder.encode("Admin@123"));

			superAdmin.setPhone("9999999999");

			superAdmin.setTimezone("Asia/Kolkata");

			superAdmin.setRole(Role.ROLE_SUPER_ADMIN);

			userRepository.save(superAdmin);

		}
	}
}
