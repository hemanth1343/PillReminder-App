package com.pillreminder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDto {

    private long activeMedications;

    private long todayReminders;

    private long missedDoses;

    private int adherence;
}