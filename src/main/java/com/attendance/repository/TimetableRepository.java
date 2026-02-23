package com.attendance.repository;

import com.attendance.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Optional<Timetable> findByClassroomIdAndSemester(Long classroomId, Integer semester);

    boolean existsByClassroomIdAndSemester(Long classroomId, Integer semester);
}
