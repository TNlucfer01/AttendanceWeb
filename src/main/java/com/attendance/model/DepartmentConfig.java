package com.attendance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "department_configs",
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DepartmentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "leave_limit_per_semester", nullable = false)
    @Builder.Default
    private Integer leaveLimitPerSemester = 15;

    @Column(name = "attendance_threshold", nullable = false)
    @Builder.Default
    private Double attendanceThreshold = 75.0;
}
