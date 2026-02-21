package com.attendance.model;

import com.attendance.model.enums.WorkingDays;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_config")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periods_per_day", nullable = false)
    @Builder.Default
    private Integer periodsPerDay = 7;

    @Enumerated(EnumType.STRING)
    @Column(name = "working_days", nullable = false)
    @Builder.Default
    private WorkingDays workingDays = WorkingDays.MON_TO_FRI;

    @Column(name = "default_attendance_threshold", nullable = false)
    @Builder.Default
    private Double defaultAttendanceThreshold = 75.0;

    @Column(name = "max_leave_limit_per_semester", nullable = false)
    @Builder.Default
    private Integer maxLeaveLimitPerSemester = 15;

    @Column(name = "max_od_days_per_semester", nullable = false)
    @Builder.Default
    private Integer maxOdDaysPerSemester = 10;

    @Column(name = "active_principal_id")
    private Long activePrincipalId;

    @Column(name = "active_academic_year_id")
    private Long activeAcademicYearId;
}
