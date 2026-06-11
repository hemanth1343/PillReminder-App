package com.pillreminder.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pillreminder.entity.User;
import com.pillreminder.entity.WaterLog;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.repository.WaterLogRepository;
import com.pillreminder.service.WaterService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl
        implements WaterService {

    private final WaterLogRepository waterLogRepository;

    private final UserRepository userRepository;

    @Override
    public List<WaterLog> getTodayWaterLogs(
            String email
    ){

        User user =
                userRepository
                .findByEmail(email)
                .orElseThrow(() ->

                        new RuntimeException(
                                "User not found"
                        )
                );

        return waterLogRepository
                .findByUser(user);
    }
    @Override
    @Transactional
    public WaterLog markTaken(
            String email,
            Long id
    ){

        WaterLog log =
                waterLogRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Water reminder not found"
                        )
                );

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime scheduled =
                log.getScheduledTime();

        if(
            now.isBefore(
                    scheduled.minusMinutes(3)
            )
        ){

            throw new RuntimeException(

                    "⏰ You can drink water only within 3 minutes before reminder time."
            );
        }

        if(
            now.isAfter(
                    scheduled.plusMinutes(3)
            )
        ){

            log.setStatus(
                    ReminderStatus.MISSED
            );

            waterLogRepository.save(log);

            throw new RuntimeException(

                    "❌ Water reminder expired and marked as MISSED."
            );
        }

        log.setStatus(
                ReminderStatus.TAKEN
        );

        log.setTakenAt(now);

        return waterLogRepository.save(log);
    }

    @Override
    @Transactional
    public WaterLog markMissed(
            String email,
            Long id
    ){

        WaterLog log =
                waterLogRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Water reminder not found"
                        )
                );

        LocalDateTime now =
                LocalDateTime.now();

        if(
            now.isBefore(
                    log.getScheduledTime()
            )
        ){

            throw new RuntimeException(

                    "⏰ Still time is there to drink water."
            );
        }

        log.setStatus(
                ReminderStatus.MISSED
        );

        return waterLogRepository.save(log);
    }

    @Override
    public void generateDailyWaterReminders() {

        List<User> users =
                userRepository.findAll();

        LocalDate today =
                LocalDate.now();

        LocalTime[] times = {

                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0)
        };

        for(User user : users){

            for(LocalTime time : times){

                LocalDateTime scheduledTime =
                        today.atTime(time);

                boolean exists =

                        waterLogRepository
                        .existsByUserAndScheduledTime(

                                user,

                                scheduledTime
                        );

                if(!exists){

                    WaterLog log =

                            WaterLog.builder()

                            .user(user)

                            .scheduledTime(
                                    scheduledTime
                            )

                            .quantityMl(250)

                            .status(
                                    ReminderStatus.PENDING
                            )

                            .emailSent(false)

                            .build();

                    waterLogRepository.save(log);

                    System.out.println(

                            "Water Reminder Created : "

                            + user.getEmail()

                            + " -> "

                            + scheduledTime
                    );
                }
            }
        }
    }
    
   
    
}
