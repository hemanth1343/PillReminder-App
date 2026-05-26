package com.pillreminder.service;

import com.pillreminder.dto.Dtos.*;

import java.time.LocalDate;
import java.util.List;

public interface ReminderService {
	List<ReminderLogResponse> getTodayReminders(String email);

	List<ReminderLogResponse> getByDateRange(String email, LocalDate from, LocalDate to);

	List<ReminderLogResponse> getPendingReminders(String email);

	ReminderLogResponse getById(String email, Long logId);

	ReminderLogResponse markTaken(String email, Long logId);

	ReminderLogResponse markMissed(String email, Long logId);

	ReminderLogResponse snooze(String email, Long logId, int minutes);

	ReminderLogResponse skip(String email, Long logId, String reason);

	AdherenceStatsResponse getAdherenceStats(String email, LocalDate from, LocalDate to);

	void generateDailyReminders(LocalDate date);

	void markOverdueAsMissed();
}
