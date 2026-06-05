package com.pillreminder.service.impl;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.*;
import com.pillreminder.enums.ReminderStatus;
import com.pillreminder.exception.ResourceNotFoundException;
import com.pillreminder.repository.*;
import com.pillreminder.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

	private final ReminderLogRepository reminderLogRepository;
	private final MedicationRepository medicationRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;
	private final MailService mailService;

	@Override
	public List<ReminderLogResponse> getTodayReminders(String email) {
		User user = findUser(email);
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
		return reminderLogRepository.findByUserIdAndDateRange(user.getId(), startOfDay, endOfDay).stream()
				.map(this::toResponse).toList();
	}

	@Override
	public List<ReminderLogResponse> getByDateRange(String email, LocalDate from, LocalDate to) {
		User user = findUser(email);
		return reminderLogRepository
				.findByUserIdAndDateRange(user.getId(), from.atStartOfDay(), to.atTime(LocalTime.MAX)).stream()
				.map(this::toResponse).toList();
	}

	@Override
	public List<ReminderLogResponse> getPendingReminders(String email) {
		User user = findUser(email);
		return reminderLogRepository.findByUserIdAndStatus(user.getId(), ReminderStatus.PENDING).stream()
				.map(this::toResponse).toList();
	}

	@Override
	public ReminderLogResponse getById(String email, Long logId) {
		User user = findUser(email);
		ReminderLog log = reminderLogRepository.findByIdAndUserId(logId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Reminder log not found: " + logId));
		return toResponse(log);
	}

	@Override
	@Transactional
	public ReminderLogResponse markTaken(String email, Long logId) {
		ReminderLog rl = findLog(email, logId);
		rl.setStatus(ReminderStatus.TAKEN);
		rl.setTakenAt(LocalDateTime.now());

		Medication med = rl.getMedication();
		if (med.getRemainingPills() != null && med.getRemainingPills() > 0) {
			med.setRemainingPills(med.getRemainingPills() - 1);
			medicationRepository.save(med);

			if (med.getRefillReminderAt() != null && med.getRemainingPills() <= med.getRefillReminderAt()) {
				notificationService.sendRefillReminder(rl.getUser(), med);
			}
		}

		return toResponse(reminderLogRepository.save(rl));
	}

	@Override
	@Transactional
	public ReminderLogResponse markMissed(String email, Long logId) {
		ReminderLog rl = findLog(email, logId);
		rl.setStatus(ReminderStatus.MISSED);
		return toResponse(reminderLogRepository.save(rl));
	}

	@Override
	@Transactional
	public ReminderLogResponse snooze(String email, Long logId, int minutes) {
		ReminderLog rl = findLog(email, logId);
		rl.setStatus(ReminderStatus.SNOOZED);
		rl.setSnoozeCount(rl.getSnoozeCount() == null ? 1 : rl.getSnoozeCount() + 1);
		rl.setScheduledTime(rl.getScheduledTime().plusMinutes(minutes));
		reminderLogRepository.save(rl);

		notificationService.scheduleSnoozeNotification(rl.getUser(), rl.getMedication(), rl.getScheduledTime());

		return toResponse(rl);
	}

	@Override
	@Transactional
	public ReminderLogResponse skip(String email, Long logId, String reason) {
		ReminderLog rl = findLog(email, logId);
		rl.setStatus(ReminderStatus.SKIPPED);
		rl.setNotes(reason);
		return toResponse(reminderLogRepository.save(rl));
	}

	@Override
	public AdherenceStatsResponse getAdherenceStats(String email, LocalDate from, LocalDate to) {
		User user = findUser(email);
		LocalDateTime start = from.atStartOfDay();
		LocalDateTime end = to.atTime(LocalTime.MAX);

		List<ReminderLog> logs = reminderLogRepository.findByUserIdAndDateRange(user.getId(), start, end);

		long total = logs.size();
		long taken = count(logs, ReminderStatus.TAKEN);
		long missed = count(logs, ReminderStatus.MISSED);
		long snoozed = count(logs, ReminderStatus.SNOOZED);
		long skipped = count(logs, ReminderStatus.SKIPPED);

		double adherence = total == 0 ? 0.0 : (taken * 100.0 / total);

		Map<Long, List<ReminderLog>> byMed = logs.stream()
				.collect(Collectors.groupingBy(r -> r.getMedication().getId()));

		List<MedicationAdherenceResponse> perMed = byMed.entrySet().stream().map(e -> {
			List<ReminderLog> ml = e.getValue();
			long mt = ml.size();
			long mk = count(ml, ReminderStatus.TAKEN);
			return MedicationAdherenceResponse.builder().medicationId(e.getKey())
					.medicationName(ml.get(0).getMedication().getName()).totalScheduled(mt).taken(mk)
					.missed(count(ml, ReminderStatus.MISSED)).adherencePercentage(mt == 0 ? 0 : mk * 100.0 / mt)
					.build();
		}).toList();

		return AdherenceStatsResponse.builder().userId(user.getId()).from(from).to(to).totalScheduled(total)
				.taken(taken).missed(missed).snoozed(snoozed).skipped(skipped).adherencePercentage(adherence)
				.perMedication(perMed).build();
	}

	@Override
	@Transactional
	public void generateDailyReminders(LocalDate date) {
		List<Medication> active = medicationRepository.findAllActiveMedicationsForToday(date);
		int created = 0;

		for (Medication med : active) {
			for (LocalTime time : med.getScheduledTimeList()) {
				LocalDateTime scheduled = date.atTime(time);

				boolean exists = reminderLogRepository
						.findByMedicationAndDateRange(med.getId(), scheduled.minusMinutes(1), scheduled.plusMinutes(1))
						.stream().anyMatch(r -> r.getScheduledTime().equals(scheduled));

				if (!exists) {
					ReminderLog rl = ReminderLog.builder().user(med.getUser()).medication(med).scheduledTime(scheduled)
							.status(ReminderStatus.PENDING).snoozeCount(0).build();
					reminderLogRepository.save(rl);
					created++;

				}
			}
		}
		log.info("Generated {} reminders for {}", created, date);
	}

	@Override
	@Transactional
	public void markOverdueAsMissed() {
		LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
		List<ReminderLog> overdue = reminderLogRepository.findOverdueReminders(threshold);
		for (ReminderLog rl : overdue) {
			rl.setStatus(ReminderStatus.MISSED);
		}
		reminderLogRepository.saveAll(overdue);
		if (!overdue.isEmpty())
			log.info("Marked {} reminders as MISSED", overdue.size());
	}

	private ReminderLog findLog(String email, Long logId) {
		User user = findUser(email);
		return reminderLogRepository.findByIdAndUserId(logId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Reminder log not found: " + logId));
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	private long count(List<ReminderLog> logs, ReminderStatus status) {
		return logs.stream().filter(l -> l.getStatus() == status).count();
	}

	ReminderLogResponse toResponse(ReminderLog r) {
		return ReminderLogResponse.builder().id(r.getId()).medicationId(r.getMedication().getId())
				.medicationName(r.getMedication().getName()).dosage(r.getMedication().getDosage())
				.scheduledTime(r.getScheduledTime()).takenAt(r.getTakenAt()).status(r.getStatus()).notes(r.getNotes())
				.snoozeCount(r.getSnoozeCount()).createdAt(r.getCreatedAt()).build();
	}

	@Transactional
	public void sendMedicationReminders() {

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime afterFiveMinutes = now.plusMinutes(5);

		List<ReminderLog> reminders = reminderLogRepository.findRemindersForEmail(now, afterFiveMinutes);

		for (ReminderLog reminder : reminders) {

			try {

				mailService.sendMail(

						reminder.getUser().getEmail(),

						reminder.getMedication().getName(),

						reminder.getMedication().getDosage(),

						reminder.getScheduledTime().toLocalTime().toString());

				reminder.setEmailSent(true);

				reminderLogRepository.save(reminder);

			} catch (Exception e) {

				log.error("Mail failed: {}", e.getMessage());
			}
		}
	}

}
