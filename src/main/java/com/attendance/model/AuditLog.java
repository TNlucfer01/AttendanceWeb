package com.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Append-only audit log. No UPDATE or DELETE operations allowed on this table.
 * All attendance modifications, leave approvals, and system actions are logged
 * here.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_actor", columnList = "actor_id, timestamp"),
        @Index(name = "idx_audit_target", columnList = "target_entity, target_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "actor_role", nullable = false)
    private String actorRole;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "target_entity", nullable = false)
    private String targetEntity;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "approved_by")
    private Long approvedBy;

    private String tag; // e.g., "Acting HOD", "Principal Delegate"
}
