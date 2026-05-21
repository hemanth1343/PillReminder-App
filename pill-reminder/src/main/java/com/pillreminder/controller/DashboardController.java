package com.pillreminder.controller;

import com.pillreminder.dto.DashboardStatsDto;
import com.pillreminder.entity.ReminderLog;
import com.pillreminder.entity.User;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.repository.MedicationRepository;
import com.pillreminder.repository.ReminderLogRepository;
import com.pillreminder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final UserRepository userRepository;

    private final MedicationRepository medicationRepository;

    private final ReminderLogRepository reminderLogRepository;

    @GetMapping("/stats")
    public DashboardStatsDto getStats(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        // =========================
        // ACTIVE MEDICATIONS
        // =========================

        long activeMedications =
                medicationRepository
                        .findByUser(user)
                        .size();

        // =========================
        // TODAY REMINDERS
        // =========================

        List<ReminderLog> logs =
                reminderLogRepository
                        .findByUser(user);

        long todayReminders =
                logs.stream()

                        .filter(log ->

                                log.getScheduledTime()
                                        .toLocalDate()
                                        .equals(
                                                LocalDateTime.now()
                                                        .toLocalDate()
                                        )
                        )

                        .count();

        // =========================
        // MISSED DOSES
        // =========================

        long missedDoses =
                logs.stream()

                        .filter(log ->

                                log.getStatus() ==
                                        ReminderStatus.MISSED
                        )

                        .count();

        // =========================
        // ADHERENCE %
        // =========================

        long taken =
                logs.stream()

                        .filter(log ->

                                log.getStatus() ==
                                        ReminderStatus.TAKEN
                        )

                        .count();

        int adherence = 0;

        if(!logs.isEmpty()){

            adherence =
                    (int)((taken * 100)
                            / logs.size());
        }

        return new DashboardStatsDto(

                activeMedications,

                todayReminders,

                missedDoses,

                adherence
        );
    }
}