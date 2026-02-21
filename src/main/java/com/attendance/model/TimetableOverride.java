package com.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetable_overrides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id", nullable = false)
    private Timetable timetable;

    @Column(name = "specific_date", nullable = false)
    private LocalDate specificDate;

    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_subject_id")
    private Subject originalSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "override_subject_id")
    private Subject overrideSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "override_staff_id")
    private Staff overrideStaff;

    private String reason;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
