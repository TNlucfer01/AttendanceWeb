package com.attendance.model;

import com.attendance.model.enums.SemesterStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_years")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_label", nullable = false)
    private String yearLabel; // e.g., "2024-2025"

    @Column(name = "semester_start", nullable = false)
    private LocalDate semesterStart;

    @Column(name = "semester_end", nullable = false)
    private LocalDate semesterEnd;

    @Column(name = "semester_length_weeks")
    private Integer semesterLengthWeeks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SemesterStatus status = SemesterStatus.UPCOMING;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
