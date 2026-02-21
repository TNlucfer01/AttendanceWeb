package com.attendance.repository;

import com.attendance.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTargetEntityAndTargetIdOrderByTimestampDesc(String targetEntity, Long targetId);

    List<AuditLog> findByActorIdOrderByTimestampDesc(Long actorId);
}
