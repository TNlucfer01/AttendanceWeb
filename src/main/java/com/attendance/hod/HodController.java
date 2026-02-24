package com.attendance.hod;

import com.attendance.dto.*;
import com.attendance.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hod")
public class HodController {

        private final HodService hodService;

        public HodController(HodService hodService) {
                this.hodService = hodService;
        }

        private @NonNull Long userId(HttpServletRequest req) {
                Long id = (Long) req.getAttribute("userId");
                if (id == null)
                        throw new RuntimeException("Unauthorized: User ID not found");
                return id;
        }

        // =========================================================================
        // Classroom Endpoints
        // =========================================================================

        @PostMapping("/classrooms")
        public ResponseEntity<Map<String, Object>> createClassRoom(
                        @Valid @RequestBody CreateClassRoomRequest request,
                        HttpServletRequest req) {
                ClassRoom room = hodService.createClassRoom(request, userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Classroom created successfully",
                                "classroomId", room.getId(),
                                "name", room.getName(),
                                "code", room.getCode(),
                                "year", room.getYear(),
                                "semester", room.getSemester()));
        }

        @GetMapping("/classrooms")
        public ResponseEntity<List<Map<String, Object>>> listClassRooms(HttpServletRequest req) {
                return ResponseEntity.ok(hodService.listClassRooms(userId(req)).stream()
                                .map(r -> Map.<String, Object>of(
                                                "id", r.getId(),
                                                "name", r.getName(),
                                                "code", r.getCode(),
                                                "year", r.getYear(),
                                                "semester", r.getSemester(),
                                                "status", r.getStatus().name(),
                                                "classAdvisor",
                                                r.getClassAdvisor() != null ? r.getClassAdvisor().getName()
                                                                : "Unassigned"))
                                .collect(Collectors.toList()));
        }

        @GetMapping("/classrooms/{id}")
        public ResponseEntity<Map<String, Object>> getClassRoom(@PathVariable Long id, HttpServletRequest req) {
                ClassRoom r = hodService.getClassRoom(id, userId(req));
                return ResponseEntity.ok(Map.of(
                                "id", r.getId(),
                                "name", r.getName(),
                                "code", r.getCode(),
                                "year", r.getYear(),
                                "semester", r.getSemester(),
                                "status", r.getStatus().name(),
                                "classAdvisor",
                                r.getClassAdvisor() != null ? r.getClassAdvisor().getName() : "Unassigned"));
        }

        @PutMapping("/classrooms/{id}/assign-advisor")
        public ResponseEntity<Map<String, Object>> assignAdvisor(
                        @PathVariable Long id,
                        @Valid @RequestBody AssignAdvisorRequest request,
                        HttpServletRequest req) {
                ClassRoom room = hodService.assignAdvisor(id, request.getStaffId(), userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Class Advisor assigned successfully",
                                "classroomId", room.getId(),
                                "advisorName", room.getClassAdvisor().getName()));
        }

        @PutMapping("/classrooms/{id}/archive")
        public ResponseEntity<Map<String, String>> archiveClassRoom(@PathVariable Long id, HttpServletRequest req) {
                hodService.archiveClassRoom(id, userId(req));
                return ResponseEntity.ok(Map.of("message", "Classroom archived successfully"));
        }

        // =========================================================================
        // Subject Endpoints
        // =========================================================================

        @PostMapping("/classrooms/{classroomId}/subjects")
        public ResponseEntity<Map<String, Object>> createSubject(
                        @PathVariable Long classroomId,
                        @Valid @RequestBody CreateSubjectRequest request,
                        HttpServletRequest req) {
                Subject s = hodService.createSubject(classroomId, request, userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Subject created successfully",
                                "subjectId", s.getId(),
                                "name", s.getName(),
                                "code", s.getCode(),
                                "type", s.getType().name(),
                                "hoursPerWeek", s.getHoursPerWeek()));
        }

        @GetMapping("/classrooms/{classroomId}/subjects")
        public ResponseEntity<List<Map<String, Object>>> listSubjects(
                        @PathVariable Long classroomId, HttpServletRequest req) {
                return ResponseEntity.ok(hodService.listSubjects(classroomId, userId(req)).stream()
                                .map(s -> Map.<String, Object>of(
                                                "id", s.getId(),
                                                "name", s.getName(),
                                                "code", s.getCode(),
                                                "type", s.getType().name(),
                                                "credits", s.getCredits(),
                                                "hoursPerWeek", s.getHoursPerWeek(),
                                                "staff", s.getStaff() != null ? s.getStaff().getName() : "Unassigned"))
                                .collect(Collectors.toList()));
        }

        @PutMapping("/subjects/{subjectId}/assign-staff")
        public ResponseEntity<Map<String, String>> assignSubjectStaff(
                        @PathVariable Long subjectId,
                        @Valid @RequestBody AssignSubjectStaffRequest request,
                        HttpServletRequest req) {
                hodService.assignSubjectStaff(subjectId, request.getStaffId(), userId(req));
                return ResponseEntity.ok(Map.of("message", "Subject staff assigned successfully"));
        }

        @DeleteMapping("/subjects/{subjectId}")
        public ResponseEntity<Map<String, String>> deactivateSubject(
                        @PathVariable Long subjectId, HttpServletRequest req) {
                hodService.deactivateSubject(subjectId, userId(req));
                return ResponseEntity.ok(Map.of("message", "Subject deactivated"));
        }

        // =========================================================================
        // Staff Endpoints
        // =========================================================================

        @PostMapping("/staff")
        public ResponseEntity<Map<String, Object>> createStaff(
                        @Valid @RequestBody CreateStaffRequest request,
                        HttpServletRequest req) {
                User user = hodService.createStaff(request, userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Staff account created successfully",
                                "userId", user.getId(),
                                "email", user.getEmail(),
                                "defaultPassword", "Welcome@123"));
        }

        @GetMapping("/staff")
        public ResponseEntity<List<Map<String, Object>>> listStaff(HttpServletRequest req) {
                return ResponseEntity.ok(hodService.listDepartmentStaff(userId(req)).stream()
                                .map(s -> Map.<String, Object>of(
                                                "id", s.getId(),
                                                "name", s.getName(),
                                                "employeeId", s.getEmployeeId(),
                                                "email", s.getUser().getEmail(),
                                                "isHod", s.getIsHod()))
                                .collect(Collectors.toList()));
        }

        @PutMapping("/staff/{staffId}/deactivate")
        public ResponseEntity<Map<String, String>> deactivateStaff(
                        @PathVariable Long staffId, HttpServletRequest req) {
                hodService.deactivateStaff(staffId, userId(req));
                return ResponseEntity.ok(Map.of("message", "Staff deactivated successfully"));
        }

        // =========================================================================
        // Timetable Endpoints
        // =========================================================================

        @PostMapping("/classrooms/{classroomId}/timetable")
        public ResponseEntity<Map<String, Object>> createTimetable(
                        @PathVariable Long classroomId,
                        @RequestParam Integer semester,
                        HttpServletRequest req) {
                Timetable tt = hodService.createTimetable(classroomId, semester, userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Timetable created",
                                "timetableId", tt.getId(),
                                "classroomId", classroomId,
                                "semester", semester));
        }

        @PostMapping("/timetables/{timetableId}/slots")
        public ResponseEntity<Map<String, Object>> addSlot(
                        @PathVariable Long timetableId,
                        @Valid @RequestBody CreateTimetableSlotRequest request,
                        HttpServletRequest req) {
                TimetableSlot slot = hodService.addTimetableSlot(timetableId, request, userId(req));
                return ResponseEntity.ok(Map.of(
                                "message", "Slot added successfully",
                                "slotId", slot.getId(),
                                "day", slot.getDay().name(),
                                "period", slot.getPeriodNumber(),
                                "type", slot.getType().name()));
        }

        @GetMapping("/timetables/{timetableId}/slots")
        public ResponseEntity<List<Map<String, Object>>> getSlots(
                        @PathVariable Long timetableId, HttpServletRequest req) {
                return ResponseEntity.ok(hodService.getTimetableSlots(timetableId, userId(req)).stream()
                                .map(s -> Map.<String, Object>of(
                                                "id", s.getId(),
                                                "day", s.getDay().name(),
                                                "period", s.getPeriodNumber(),
                                                "startTime", s.getStartTime().toString(),
                                                "endTime", s.getEndTime().toString(),
                                                "type", s.getType().name(),
                                                "subject", s.getSubject() != null ? s.getSubject().getName() : "-",
                                                "staff", s.getStaff() != null ? s.getStaff().getName() : "TBA"))
                                .collect(Collectors.toList()));
        }

        @DeleteMapping("/timetables/{timetableId}/slots/{slotId}")
        public ResponseEntity<Map<String, String>> deleteSlot(
                        @PathVariable Long timetableId,
                        @PathVariable Long slotId,
                        HttpServletRequest req) {
                hodService.deleteTimetableSlot(slotId, userId(req));
                return ResponseEntity.ok(Map.of("message", "Slot deleted"));
        }
}
