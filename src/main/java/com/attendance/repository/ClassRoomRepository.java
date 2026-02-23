package com.attendance.repository;

import com.attendance.model.ClassRoom;
import com.attendance.model.enums.ClassRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    boolean existsByNameAndDepartmentIdAndYearAndSemester(String name, Long departmentId, Integer year, Integer semester);
    boolean existsByCode(String code);
    List<ClassRoom> findByDepartmentId(Long departmentId);
    List<ClassRoom> findByDepartmentIdAndStatus(Long departmentId, ClassRoomStatus status);
    List<ClassRoom> findByClassAdvisorId(Long staffId);

    @Query("SELECT COUNT(c) FROM ClassRoom c WHERE c.classAdvisor.id = :staffId AND c.status = 'ACTIVE'")
    long countActiveClassesByAdvisor(Long staffId);

    Optional<ClassRoom> findByCode(String code);
}
