package com.pillreminder.repository;

import com.pillreminder.entity.ReminderLog;
import com.pillreminder.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.pillreminder.entity.User;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {

    List<ReminderLog> findByUserIdOrderByScheduledTimeDesc(Long userId);

    List<ReminderLog> findByUserIdAndStatus(Long userId, ReminderStatus status);

    List<ReminderLog> findByMedicationIdAndStatus(Long medicationId, ReminderStatus status);

    @Query("SELECT r FROM ReminderLog r WHERE r.user.id = :userId " +
           "AND r.scheduledTime BETWEEN :start AND :end " +
           "ORDER BY r.scheduledTime ASC")
    List<ReminderLog> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT r FROM ReminderLog r WHERE r.status = 'PENDING' " +
           "AND r.scheduledTime <= :now")
    List<ReminderLog> findOverdueReminders(@Param("now") LocalDateTime now);

    Optional<ReminderLog> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(r) FROM ReminderLog r WHERE r.user.id = :userId " +
           "AND r.status = :status " +
           "AND r.scheduledTime BETWEEN :start AND :end")
    long countByUserAndStatusAndDateRange(
            @Param("userId") Long userId,
            @Param("status") ReminderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT r FROM ReminderLog r WHERE r.medication.id = :medicationId " +
           "AND r.scheduledTime BETWEEN :start AND :end " +
           "ORDER BY r.scheduledTime DESC")
    List<ReminderLog> findByMedicationAndDateRange(
            @Param("medicationId") Long medicationId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    List<ReminderLog> findByUser(User user);
}
