package com.pillreminder.dto;


import java.time.LocalDateTime;

import com.pillreminder.enums.ReminderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WaterLogResponse {

    private Long id;

    private LocalDateTime scheduledTime;

    private ReminderStatus status;

    private Integer quantityMl;
}
