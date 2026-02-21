package com.attendance.model;

import com.attendance.model.enums.DayOfWeekEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "saturday_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaturdayConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saturday_date", nullable = false, unique = true)
    private LocalDate saturdayDate;

    @Column(name = "is_working_day", nullable = false)
    @Builder.Default
    private Boolean isWorkingDay = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "cyclic_weekday")
    private DayOfWeekEnum cyclicWeekday;

    @Column(name = "is_overridden", nullable = false)
    @Builder.Default
    private Boolean isOverridden = false;

    @Column(name = "enabled_by")
    private Long enabledBy;

    @Column(name = "enabled_at")
    private java.time.LocalDateTime enabledAt;
}
