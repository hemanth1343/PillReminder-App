package com.pillreminder.scheduler;

import com.pillreminder.service.ReminderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderService reminderService;

    // =========================================
    // Generate today's reminders at midnight
    // =========================================

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyReminders() {

        log.info(
                "Generating daily reminders for {}",
                LocalDate.now()
        );

        reminderService.generateDailyReminders(
                LocalDate.now()
        );
    }

    // =========================================
    // Pre-generate tomorrow reminders at 11 PM
    // =========================================

    @Scheduled(cron = "0 0 23 * * *")
    public void generateTomorrowReminders() {

        LocalDate tomorrow =
                LocalDate.now().plusDays(1);

        log.info(
                "Pre-generating reminders for {}",
                tomorrow
        );

        reminderService.generateDailyReminders(
                tomorrow
        );
    }

    // =========================================
    // Real-time reminder generation every minute
    // =========================================

    @Scheduled(fixedRate = 60000)
    public void generateCurrentReminders() {

        try {

            reminderService.generateDailyReminders(
                    LocalDate.now()
            );

            log.info(
                    "✅ Real-time reminder generation executed"
            );

        } catch (Exception e) {

            log.error(
                    "❌ Real-time reminder generation failed: {}",
                    e.getMessage()
            );
        }
    }

    // =========================================
    // Mark overdue reminders as missed
    // =========================================

    @Scheduled(
            initialDelay = 120000,
            fixedRate = 900000
    )
    public void checkOverdueReminders() {

        try {

            reminderService.markOverdueAsMissed();

        } catch (Exception e) {

            log.warn(
                    "Overdue check skipped: {}",
                    e.getMessage()
            );
        }
    }
}