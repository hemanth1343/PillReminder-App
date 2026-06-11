package com.pillreminder.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pillreminder.entity.WaterLog;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.repository.WaterLogRepository;
import com.pillreminder.service.NotificationService;
import com.pillreminder.service.WaterService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaterScheduler {

    private final WaterLogRepository waterLogRepository;

    private final NotificationService notificationService;
    
    private final WaterService waterService;
    
    @Scheduled(fixedRate = 60000)
    public void sendWaterReminderEmails() {

        try {

            LocalDateTime now =
                    LocalDateTime.now();

            LocalDateTime afterThreeMinutes =
                    now.plusMinutes(3);

            List<WaterLog> reminders =
                    waterLogRepository
                    .findUpcomingWaterReminders(
                            now,
                            afterThreeMinutes
                    );

            System.out.println(
                    "Found Water Reminders: "
                    + reminders.size()
            );

            for (WaterLog reminder : reminders) {

                notificationService
                        .sendWaterReminderNotification(

                                reminder.getUser(),

                                reminder.getScheduledTime()
                        );

                reminder.setEmailSent(true);

                waterLogRepository.save(reminder);

                System.out.println(

                        "Water Mail Sent To: "

                                + reminder
                                .getUser()
                                .getEmail()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 60000)
    public void markExpiredWaterReminders(){

        LocalDateTime now =
                LocalDateTime.now();

        List<WaterLog> pending =

                waterLogRepository
                .findByStatus(
                        ReminderStatus.PENDING
                );

        for(WaterLog log : pending){

            if(
                now.isAfter(

                    log.getScheduledTime()
                       .plusMinutes(3)
                )
            ){

                log.setStatus(
                        ReminderStatus.MISSED
                );

                waterLogRepository.save(log);
            }
        }
    }
    
    @PostConstruct
    public void init() {

        System.out.println(
            "WATER SCHEDULER STARTED"
        );

		waterService.generateDailyWaterReminders();
    }
    
}