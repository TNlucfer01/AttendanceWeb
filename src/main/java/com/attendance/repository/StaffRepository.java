package com.attendance.repository;

import com.attendance.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    List<Staff> findByDepartmentId(Long departmentId);

    List<Staff> findByDepartmentIdAndIsActiveTrue(Long departmentId);

    Optional<Staff> findByUserEmail(String email);

    List<Staff> findByIsHodTrue();
}
