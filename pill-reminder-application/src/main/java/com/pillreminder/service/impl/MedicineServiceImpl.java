package com.pillreminder.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillreminder.dto.MedicineResponse;
import com.pillreminder.entity.Medicine;
import com.pillreminder.repository.MedicineRepository;
import com.pillreminder.service.MedicineService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

	private final MedicineRepository medicineRepository;

	private final RestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public MedicineResponse getMedicine(String medicineName) {

		Medicine cached = medicineRepository.findByMedicineNameIgnoreCase(medicineName).orElse(null);

		if (cached != null) {

			return map(cached);
		}

		try {

			String url =

					"https://api.fda.gov/drug/label.json" + "?search=openfda.brand_name:\"" + medicineName
							+ "\"&limit=1";

			String response =

					restTemplate.getForObject(url, String.class);

			JsonNode root = objectMapper.readTree(response);

			JsonNode result = root.path("results").get(0);

			Medicine medicine = Medicine.builder()

					.medicineName(medicineName)

					.genericName(getNestedArrayValue(result, "openfda", "generic_name"))

					.manufacturer(getNestedArrayValue(result, "openfda", "manufacturer_name"))

					.uses(getArrayValue(result, "indications_and_usage"))

					.sideEffects(getArrayValue(result, "adverse_reactions"))

					.dosage(getArrayValue(result, "dosage_and_administration"))

					.interactions(getArrayValue(result, "drug_interactions"))

					.warnings(getArrayValue(result, "warnings"))

					.whoCanTake("Consult Healthcare Professional")

					.whoShouldAvoid(getArrayValue(result, "contraindications"))

					.storage(getArrayValue(result, "storage_and_handling"))

					.lastUpdated(LocalDateTime.now())

					.build();

			medicineRepository.save(medicine);

			return map(medicine);

		} catch (Exception e) {

			throw new RuntimeException("Medicine not found");
		}
	}

	private String getArrayValue(JsonNode node, String field) {

		JsonNode value = node.path(field);

		if (value.isArray() && value.size() > 0) {

			return value.get(0).asText();
		}

		return "Not Available";
	}

	private String getNestedArrayValue(JsonNode node, String parent, String child) {

		JsonNode value =

				node.path(parent).path(child);

		if (value.isArray() && value.size() > 0) {

			return value.get(0).asText();
		}

		return "Not Available";
	}

	private MedicineResponse map(Medicine m) {

		return MedicineResponse.builder()

				.medicineName(m.getMedicineName())

				.genericName(m.getGenericName())

				.uses(m.getUses())

				.sideEffects(m.getSideEffects())

				.dosage(m.getDosage())

				.whoCanTake(m.getWhoCanTake())

				.whoShouldAvoid(m.getWhoShouldAvoid())

				.interactions(m.getInteractions())

				.warnings(m.getWarnings())

				.manufacturer(m.getManufacturer())

				.storage(m.getStorage())

				.build();
	}
}