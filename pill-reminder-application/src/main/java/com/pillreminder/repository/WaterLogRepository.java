package com.pillreminder.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pillreminder.entity.User;
import com.pillreminder.entity.WaterLog;
import com.pillreminder.enums.ReminderStatus;

public interface WaterLogRepository
        extends JpaRepository<WaterLog, Long> {

    List<WaterLog> findByUser(
            User user
    );

    List<WaterLog> findByStatus(
            ReminderStatus status
    );

    boolean existsByUserAndScheduledTime(
            User user,
            LocalDateTime scheduledTime
    );
    

    List<WaterLog> findByUserAndScheduledTimeBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""

        SELECT w

        FROM WaterLog w

        WHERE w.status='PENDING'

        AND w.emailSent=false

        AND w.scheduledTime

        BETWEEN :now AND :afterTime

        """)
    List<WaterLog> findUpcomingWaterReminders(

            @Param("now")
            LocalDateTime now,

            @Param("afterTime")
            LocalDateTime afterTime
    );
}