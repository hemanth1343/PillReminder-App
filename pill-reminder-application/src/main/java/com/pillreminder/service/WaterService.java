package com.pillreminder.service;


import java.util.List;

import com.pillreminder.entity.WaterLog;

public interface WaterService {

    List<WaterLog> getTodayWaterLogs(
            String email
    );

    WaterLog markTaken(
            String email,
            Long id
    );

    WaterLog markMissed(
            String email,
            Long id
    );

    void generateDailyWaterReminders();
}