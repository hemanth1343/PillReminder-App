package com.pillreminder.repository;

import com.pillreminder.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.pillreminder.entity.User;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

	List<Medication> findByUserIdAndActiveTrue(Long userId);

	List<Medication> findByUserId(Long userId);

	Optional<Medication> findByIdAndUserId(Long id, Long userId);

	@Query("SELECT m FROM Medication m WHERE m.active = true " + "AND m.startDate <= :today "
			+ "AND (m.endDate IS NULL OR m.endDate >= :today)")
	List<Medication> findAllActiveMedicationsForToday(@Param("today") LocalDate today);

	@Query("SELECT m FROM Medication m WHERE m.user.id = :userId " + "AND m.active = true "
			+ "AND m.remainingPills IS NOT NULL " + "AND m.remainingPills <= m.refillReminderAt")
	List<Medication> findMedicationsNeedingRefill(@Param("userId") Long userId);
	List<Medication> findByUser(User user);
}
