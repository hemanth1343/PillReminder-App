package com.pillreminder.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pillreminder.entity.ReminderLog;
import com.pillreminder.repository.ReminderLogRepository;
import com.pillreminder.service.MailService;
import com.pillreminder.service.ReminderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderService reminderService;
    private final ReminderLogRepository reminderLogRepository;

    private final MailService mailService;

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
    
    @Scheduled(fixedRate = 60000)

    public void sendMedicationReminders(){

        try{

            LocalDateTime now =
                    LocalDateTime.now();

            LocalDateTime afterFiveMinutes =
                    now.plusMinutes(5);

            List<ReminderLog> reminders =

                    reminderLogRepository
                    .findUpcomingReminders(

                            now,

                            afterFiveMinutes
                    );

            for(ReminderLog reminder : reminders){

                // SEND MAIL

                mailService.sendMail(

                        reminder
                        .getUser()
                        .getEmail(),

                        reminder
                        .getMedication()
                        .getName(),

                        reminder
                        .getMedication()
                        .getDosage(),

                        reminder
                        .getScheduledTime()
                        .toString()
                );

                // MARK AS SENT

                reminder.setEmailSent(true);

                reminderLogRepository.save(
                        reminder
                );

                log.info(

                        "✅ Reminder Mail Sent To {}",

                        reminder
                        .getUser()
                        .getEmail()
                );
            }

        }

        catch(Exception e){

            log.error(

                    "❌ Reminder mail failed: {}",

                    e.getMessage()
            );
        }
    }
}