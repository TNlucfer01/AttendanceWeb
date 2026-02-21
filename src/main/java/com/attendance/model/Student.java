package com.attendance.model;

import com.attendance.model.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    private Long id; // Same as user.id

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "roll_no", unique = true, nullable = false)
    private String rollNo;

    @Column(nullable = false)
    private String name;

    @Column(name = "current_year", nullable = false)
    private Integer currentYear;

    @Column(name = "batch_year", nullable = false)
    private Integer batchYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private String gender;

    private String phone;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "blood_group")
    private String bloodGroup;

    private String community;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(name = "suspension_start")
    private LocalDate suspensionStart;

    @Column(name = "suspension_end")
    private LocalDate suspensionEnd;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
