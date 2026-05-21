package com.pillreminder.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pillreminder.enums.Frequency;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 100)
    private String dosage;          // e.g., "500mg", "2 tablets"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;      // null = ongoing

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(length = 1000)
    private String instructions;    // e.g., "Take with food"

    @Column(length = 100)
    private String color;           // pill color for UI

    @Column(length = 100)
    private String shape;           // pill shape for UI

    private Integer totalPills;     // inventory count
    private Integer remainingPills;
    private Integer refillReminderAt; // remind when X pills remain

    // Scheduled times stored as comma-separated HH:mm (e.g. "08:00,14:00,20:00")
    @Column(length = 500)
    private String scheduledTimes;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReminderLog> reminderLogs = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper: parse scheduledTimes string into list of LocalTime
    @Transient
    public List<LocalTime> getScheduledTimeList() {
        if (scheduledTimes == null || scheduledTimes.isBlank()) return List.of();
        List<LocalTime> times = new ArrayList<>();
        for (String t : scheduledTimes.split(",")) {
            times.add(LocalTime.parse(t.trim()));
        }
        return times;
    }
}
