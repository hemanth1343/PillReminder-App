package com.pillreminder.service;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.*;
import com.pillreminder.enums.*;
import com.pillreminder.exception.ResourceNotFoundException;
import com.pillreminder.repository.*;
import com.pillreminder.service.impl.ReminderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock private ReminderLogRepository reminderLogRepository;
    @Mock private MedicationRepository medicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks private ReminderServiceImpl reminderService;

    private User user;
    private Medication medication;
    private ReminderLog reminderLog;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@example.com").fullName("Test User").build();

        medication = Medication.builder()
                .id(1L)
                .user(user)
                .name("Aspirin")
                .dosage("100mg")
                .remainingPills(30)
                .refillReminderAt(5)
                .scheduledTimes("08:00")
                .build();

        reminderLog = ReminderLog.builder()
                .id(1L)
                .user(user)
                .medication(medication)
                .scheduledTime(LocalDateTime.now())
                .status(ReminderStatus.PENDING)
                .snoozeCount(0)
                .build();
    }

    @Test
    @DisplayName("Mark taken - updates status and decrements pill count")
    void markTaken_updatesStatusAndDecrements() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(reminderLogRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(reminderLog));
        when(reminderLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(medicationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReminderLogResponse response = reminderService.markTaken("user@example.com", 1L);

        assertThat(response.getStatus()).isEqualTo(ReminderStatus.TAKEN);
        assertThat(reminderLog.getTakenAt()).isNotNull();
        assertThat(medication.getRemainingPills()).isEqualTo(29);
    }

    @Test
    @DisplayName("Snooze - increments snooze count and advances scheduled time")
    void snooze_incrementsCountAndPushesTime() {
        LocalDateTime original = reminderLog.getScheduledTime();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(reminderLogRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(reminderLog));
        when(reminderLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReminderLogResponse response = reminderService.snooze("user@example.com", 1L, 10);

        assertThat(response.getStatus()).isEqualTo(ReminderStatus.SNOOZED);
        assertThat(reminderLog.getSnoozeCount()).isEqualTo(1);
        assertThat(reminderLog.getScheduledTime()).isEqualTo(original.plusMinutes(10));
    }

    @Test
    @DisplayName("Mark missed - not found throws exception")
    void markMissed_notFound_throws() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(reminderLogRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderService.markMissed("user@example.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Adherence stats - calculates percentage correctly")
    void adherenceStats_calculatesCorrectly() {
        ReminderLog taken = ReminderLog.builder()
                .id(2L).user(user).medication(medication)
                .scheduledTime(LocalDateTime.now()).status(ReminderStatus.TAKEN).build();
        ReminderLog missed = ReminderLog.builder()
                .id(3L).user(user).medication(medication)
                .scheduledTime(LocalDateTime.now()).status(ReminderStatus.MISSED).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(reminderLogRepository.findByUserIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(taken, missed));

        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to   = LocalDate.now();
        AdherenceStatsResponse stats = reminderService.getAdherenceStats("user@example.com", from, to);

        assertThat(stats.getTotalScheduled()).isEqualTo(2);
        assertThat(stats.getTaken()).isEqualTo(1);
        assertThat(stats.getMissed()).isEqualTo(1);
        assertThat(stats.getAdherencePercentage()).isEqualTo(50.0);
    }
}
