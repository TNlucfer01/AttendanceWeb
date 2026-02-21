package com.attendance.repository;

import com.attendance.model.Department;
import com.attendance.model.enums.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<Department> findByCode(String code);

    List<Department> findByStatus(DepartmentStatus status);

    List<Department> findByStatusNot(DepartmentStatus status);
}
