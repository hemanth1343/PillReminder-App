package com.pillreminder.service;

import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.*;
import com.pillreminder.enums.Frequency;
import com.pillreminder.exception.ResourceNotFoundException;
import com.pillreminder.repository.*;
import com.pillreminder.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock private MedicationRepository medicationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private MedicationServiceImpl medicationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("Test User")
                .build();
    }

    @Test
    @DisplayName("Create medication - success")
    void createMedication_success() {
        CreateMedicationRequest req = new CreateMedicationRequest();
        req.setName("Aspirin");
        req.setDosage("100mg");
        req.setFrequency(Frequency.DAILY);
        req.setStartDate(LocalDate.now());
        req.setScheduledTimes(List.of("08:00"));

        Medication saved = Medication.builder()
                .id(1L)
                .user(user)
                .name("Aspirin")
                .dosage("100mg")
                .frequency(Frequency.DAILY)
                .startDate(LocalDate.now())
                .scheduledTimes("08:00")
                .active(true)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(medicationRepository.save(any())).thenReturn(saved);

        MedicationResponse response = medicationService.create("user@example.com", req);

        assertThat(response.getName()).isEqualTo("Aspirin");
        assertThat(response.getDosage()).isEqualTo("100mg");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("Get medication by ID - not found throws")
    void getMedicationById_notFound_throws() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(medicationRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicationService.getById("user@example.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Soft delete sets active = false")
    void deleteMedication_setsInactive() {
        Medication med = Medication.builder()
                .id(1L)
                .user(user)
                .name("Aspirin")
                .active(true)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(medicationRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(med));
        when(medicationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        medicationService.delete("user@example.com", 1L);

        assertThat(med.isActive()).isFalse();
        verify(medicationRepository).save(med);
    }
}
