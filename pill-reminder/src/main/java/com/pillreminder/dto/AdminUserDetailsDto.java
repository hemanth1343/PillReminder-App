package com.pillreminder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailsDto {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private List<String> medications;

    private List<String> reminders;
}
