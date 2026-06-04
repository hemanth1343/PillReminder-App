package com.pillreminder.service;

import com.pillreminder.dto.Dtos.*;

public interface UserService {
	UserResponse getCurrentUser(String email);

	UserResponse updateUser(String email, UpdateUserRequest request);

	void changePassword(String email, ChangePasswordRequest request);

	void deleteAccount(String email);
	
	void changePasswordAfterOtp(
	        String email,
	        String newPassword
	);
	
	void resetPassword(
	        String email,
	        String newPassword
	);
	
}
