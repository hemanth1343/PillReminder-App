package com.pillreminder.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.pillreminder.enums.Frequency;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Dtos {

    // ─── Auth ────────────────────────────────────────────────────────────────

    @Data
    public static class RegisterRequest {
        @NotBlank @Email private String email;
        @NotBlank @Size(min = 8) private String password;
        @NotBlank private String fullName;
        private String phone;
        private String timezone;
    }

    @Data
    public static class LoginRequest {
        @NotBlank @Email private String email;
        @NotBlank private String password;
    }

    @Data @Builder
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private UserResponse user;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank private String refreshToken;
    }

    // ─── User ─────────────────────────────────────────────────────────────────

    @Data @Builder
    public static class UserResponse {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
        private Role role;
        private boolean emailNotifications;
        private boolean pushNotifications;
        private String timezone;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateUserRequest {
        private String fullName;
        private String phone;
        private String timezone;
        private Boolean emailNotifications;
        private Boolean pushNotifications;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank private String currentPassword;
        @NotBlank @Size(min = 8) private String newPassword;
    }

    // ─── Medication ───────────────────────────────────────────────────────────

    @Data
    public static class CreateMedicationRequest {
        @NotBlank private String name;
        private String description;
        @NotBlank private String dosage;
        @NotNull private Frequency frequency;
        @NotNull private LocalDate startDate;
        private LocalDate endDate;
        private String instructions;
        private String color;
        private String shape;
        private Integer totalPills;
        private Integer refillReminderAt;
        @NotEmpty private List<String> scheduledTimes;
    }

    @Data
    public static class UpdateMedicationRequest {
        private String name;
        private String description;
        private String dosage;
        private Frequency frequency;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructions;
        private String color;
        private String shape;
        private Integer totalPills;
        private Integer remainingPills;
        private Integer refillReminderAt;
        private List<String> scheduledTimes;
        private Boolean active;
    }

    @Data @Builder
    public static class MedicationResponse {
        private Long id;
        private String name;
        private String description;
        private String dosage;
        private Frequency frequency;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean active;
        private String instructions;
        private String color;
        private String shape;
        private Integer totalPills;
        private Integer remainingPills;
        private Integer refillReminderAt;
        private List<String> scheduledTimes;
        private LocalDateTime createdAt;
    }

    // ─── Reminder ─────────────────────────────────────────────────────────────

    @Data @Builder
    public static class ReminderLogResponse {
        private Long id;
        private Long medicationId;
        private String medicationName;
        private String dosage;
        private LocalDateTime scheduledTime;
        private LocalDateTime takenAt;
        private ReminderStatus status;
        private String notes;
        private Integer snoozeCount;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateReminderStatusRequest {
        @NotNull private ReminderStatus status;
        private String notes;
    }

    // ─── Stats ────────────────────────────────────────────────────────────────

    @Data @Builder
    public static class AdherenceStatsResponse {
        private Long userId;
        private LocalDate from;
        private LocalDate to;
        private long totalScheduled;
        private long taken;
        private long missed;
        private long snoozed;
        private long skipped;
        private double adherencePercentage;
        private List<MedicationAdherenceResponse> perMedication;
    }

    @Data @Builder
    public static class MedicationAdherenceResponse {
        private Long medicationId;
        private String medicationName;
        private long totalScheduled;
        private long taken;
        private long missed;
        private double adherencePercentage;
    }
    
}
