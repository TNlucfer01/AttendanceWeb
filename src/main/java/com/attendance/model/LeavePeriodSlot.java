package com.attendance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_period_slots")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LeavePeriodSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;
}
