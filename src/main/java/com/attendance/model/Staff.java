package com.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    private Long id; // Same as user.id (shared PK)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "is_class_advisor", nullable = false)
    @Builder.Default
    private Boolean isClassAdvisor = false;

    @Column(name = "is_hod", nullable = false)
    @Builder.Default
    private Boolean isHod = false;

    @Column(name = "is_acting_hod", nullable = false)
    @Builder.Default
    private Boolean isActingHod = false;

    @Column(name = "acting_hod_start")
    private LocalDateTime actingHodStart;

    @Column(name = "acting_hod_end")
    private LocalDateTime actingHodEnd;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
