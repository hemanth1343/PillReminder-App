package com.pillreminder.service;

import com.pillreminder.dto.Dtos.*;

import java.util.List;

public interface MedicationService {
	MedicationResponse create(String email, CreateMedicationRequest request);

	MedicationResponse getById(String email, Long id);

	List<MedicationResponse> getAll(String email, boolean activeOnly);

	MedicationResponse update(String email, Long id, UpdateMedicationRequest request);

	void delete(String email, Long id);

	List<MedicationResponse> getMedicationsNeedingRefill(String email);
}
