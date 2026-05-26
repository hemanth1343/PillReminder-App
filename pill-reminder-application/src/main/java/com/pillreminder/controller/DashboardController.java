package com.pillreminder.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.dto.DashboardStatsDto;
import com.pillreminder.entity.ReminderLog;
import com.pillreminder.entity.User;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.repository.MedicationRepository;
import com.pillreminder.repository.ReminderLogRepository;
import com.pillreminder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
    
    @GetMapping("/analytics")

    public ResponseEntity<?> getAnalytics(

            Authentication authentication
    ){

        String email =
                authentication.getName();

        User user =
                userRepository
                .findByEmail(email)
                .orElseThrow();

        // =========================
        // TOTAL STATUS COUNTS
        // =========================

        long taken =
                reminderLogRepository
                .countByUserAndStatus(

                        user,

                        ReminderStatus.TAKEN
                );

        long missed =
                reminderLogRepository
                .countByUserAndStatus(

                        user,

                        ReminderStatus.MISSED
                );

        long pending =
                reminderLogRepository
                .countByUserAndStatus(

                        user,

                        ReminderStatus.PENDING
                );

        // =========================
        // WEEKLY ACTIVITY
        // LAST 7 DAYS
        // =========================
        LocalDate today =
                LocalDate.now();

        List<String> labels =
                new ArrayList<>();

        List<Long> weekly =
                new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEE");

        for(int i = 6; i >= 0; i--){

            LocalDate date =
                    today.minusDays(i);

            labels.add(

                    date.format(formatter)
            );

            weekly.add(

                    reminderLogRepository
                    .countTakenByExactDate(

                            user,

                            date
                    )
            );
        }

        // =========================
        // RESPONSE
        // =========================

        Map<String,Object> map =
                new HashMap<>();

        map.put(
                "taken",
                taken
        );

        map.put(
                "missed",
                missed
        );

        map.put(
                "pending",
                pending
        );

        map.put(
                "weeklyActivity",
                weekly
        );
        map.put(
                "labels",
                labels
        );

        return ResponseEntity.ok(
                map
        );
    }
}