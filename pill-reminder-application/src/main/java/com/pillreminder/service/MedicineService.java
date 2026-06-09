package com.pillreminder.service;

import com.pillreminder.dto.MedicineResponse;

public interface MedicineService {

    MedicineResponse getMedicine(
            String medicineName
    );
}
