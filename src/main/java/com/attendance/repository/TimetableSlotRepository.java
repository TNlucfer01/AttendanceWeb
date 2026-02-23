package com.attendance.repository;

import com.attendance.model.TimetableSlot;
import com.attendance.model.enums.DayOfWeekEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findByTimetableId(Long timetableId);

    List<TimetableSlot> findByTimetableIdAndDay(Long timetableId, DayOfWeekEnum day);

    // Conflict check: is this staff already assigned to another class at this time?
    @Query("""
                SELECT COUNT(ts) > 0 FROM TimetableSlot ts
                WHERE ts.staff.id = :staffId
                AND ts.day = :day
                AND ts.periodNumber = :periodNumber
                AND ts.timetable.id != :excludeTimetableId
            """)
    boolean existsConflict(Long staffId, DayOfWeekEnum day, Integer periodNumber, Long excludeTimetableId);

    List<TimetableSlot> findByStaffId(Long staffId);
}
