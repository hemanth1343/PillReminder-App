package com.pillreminder.service.impl;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.*;

import com.pillreminder.exception.ResourceNotFoundException;

import com.pillreminder.repository.*;

import com.pillreminder.service.MedicationService;
import com.pillreminder.service.ReminderService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

	private final MedicationRepository medicationRepository;

	private final UserRepository userRepository;

	private final ReminderService reminderService;

	@Override
	@Transactional
	public MedicationResponse create(String email, CreateMedicationRequest req) {

		User user = findUser(email);

		Medication med = Medication.builder()

				.user(user)

				.name(req.getName())

				.description(req.getDescription())

				.dosage(req.getDosage())

				.frequency(req.getFrequency())

				.startDate(req.getStartDate())

				.endDate(req.getEndDate())

				.instructions(req.getInstructions())

				.color(req.getColor())

				.shape(req.getShape())

				.totalPills(req.getTotalPills())

				.remainingPills(req.getTotalPills())

				.refillReminderAt(req.getRefillReminderAt())

				.scheduledTimes(String.join(",", req.getScheduledTimes())).active(true)

				.build();

		Medication savedMedication = medicationRepository.save(med);

		reminderService.generateDailyReminders(LocalDate.now());

		return toResponse(savedMedication);
	}

	@Override
	public MedicationResponse getById(String email, Long id) {

		User user = findUser(email);

		Medication med = medicationRepository.findByIdAndUserId(id, user.getId())

				.orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + id));

		return toResponse(med);
	}

	@Override
	public List<MedicationResponse> getAll(String email, boolean activeOnly) {

		User user = findUser(email);

		List<Medication> meds = activeOnly

				?

				medicationRepository.findByUserIdAndActiveTrue(user.getId())

				:

				medicationRepository.findByUserId(user.getId());

		return meds.stream()

				.map(this::toResponse)

				.toList();
	}

	@Override
	@Transactional
	public MedicationResponse update(String email, Long id, UpdateMedicationRequest req) {

		User user = findUser(email);

		Medication med = medicationRepository.findByIdAndUserId(id, user.getId())

				.orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + id));

		if (req.getName() != null)
			med.setName(req.getName());

		if (req.getDescription() != null)
			med.setDescription(req.getDescription());

		if (req.getDosage() != null)
			med.setDosage(req.getDosage());

		if (req.getFrequency() != null)
			med.setFrequency(req.getFrequency());

		if (req.getStartDate() != null)
			med.setStartDate(req.getStartDate());

		if (req.getEndDate() != null)
			med.setEndDate(req.getEndDate());

		if (req.getInstructions() != null)
			med.setInstructions(req.getInstructions());

		if (req.getColor() != null)
			med.setColor(req.getColor());

		if (req.getShape() != null)
			med.setShape(req.getShape());

		if (req.getTotalPills() != null)
			med.setTotalPills(req.getTotalPills());

		if (req.getRemainingPills() != null)
			med.setRemainingPills(req.getRemainingPills());

		if (req.getRefillReminderAt() != null)
			med.setRefillReminderAt(req.getRefillReminderAt());

		if (req.getActive() != null)
			med.setActive(req.getActive());

		if (req.getScheduledTimes() != null)

			med.setScheduledTimes(

					String.join(",", req.getScheduledTimes()));

		Medication updated = medicationRepository.save(med);

		reminderService.generateDailyReminders(LocalDate.now());

		return toResponse(updated);
	}

	@Override
	@Transactional
	public void delete(String email, Long id) {

		User user = findUser(email);

		Medication med = medicationRepository.findByIdAndUserId(id, user.getId())

				.orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + id));

		med.setActive(false);

		medicationRepository.save(med);
	}

	@Override
	public List<MedicationResponse> getMedicationsNeedingRefill(String email) {

		User user = findUser(email);

		return medicationRepository.findMedicationsNeedingRefill(user.getId())

				.stream()

				.map(this::toResponse)

				.toList();
	}

	private User findUser(String email) {

		return userRepository.findByEmail(email)

				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	MedicationResponse toResponse(Medication m) {

		return MedicationResponse.builder()

				.id(m.getId())

				.name(m.getName())

				.description(m.getDescription())

				.dosage(m.getDosage())

				.frequency(m.getFrequency())

				.startDate(m.getStartDate())

				.endDate(m.getEndDate())

				.active(m.isActive())

				.instructions(m.getInstructions())

				.color(m.getColor())

				.shape(m.getShape())

				.totalPills(m.getTotalPills())

				.remainingPills(m.getRemainingPills())

				.refillReminderAt(m.getRefillReminderAt())

				.scheduledTimes(m.getScheduledTimeList()

						.stream()

						.map(Object::toString)

						.toList())

				.createdAt(m.getCreatedAt())

				.build();
	}
}