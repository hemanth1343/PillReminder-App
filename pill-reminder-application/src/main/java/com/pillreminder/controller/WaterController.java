package com.pillreminder.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.dto.WaterLogResponse;
import com.pillreminder.entity.WaterLog;
import com.pillreminder.service.WaterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterController {

    private final WaterService waterService;

    @GetMapping
    public List<WaterLogResponse> getWaterLogs(
            Principal principal
    ){

        return waterService
                .getTodayWaterLogs(
                        principal.getName()
                )
                .stream()
                .map(log ->

                        new WaterLogResponse(

                                log.getId(),

                                log.getScheduledTime(),

                                log.getStatus(),

                                log.getQuantityMl()
                        )
                )
                .toList();
    }

    @PostMapping("/{id}/take")
    public WaterLog markTaken(

            @PathVariable
            Long id,

            Principal principal
    ){

        return waterService.markTaken(

                principal.getName(),

                id
        );
    }

    @PostMapping("/{id}/miss")
    public WaterLog markMissed(

            @PathVariable
            Long id,

            Principal principal
    ){

        return waterService.markMissed(

                principal.getName(),

                id
        );
    }
}