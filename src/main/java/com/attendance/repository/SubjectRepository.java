package com.attendance.repository;

import com.attendance.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByCodeAndClassroomId(String code, Long classroomId);

    List<Subject> findByClassroomId(Long classroomId);

    List<Subject> findByClassroomIdAndIsActiveTrue(Long classroomId);

    List<Subject> findByStaffId(Long staffId);

    List<Subject> findByDepartmentId(Long departmentId);
}
