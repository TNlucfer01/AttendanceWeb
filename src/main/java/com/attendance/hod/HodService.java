package com.attendance.hod;

import com.attendance.dto.*;
import com.attendance.model.*;
import com.attendance.model.enums.*;
import com.attendance.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class HodService {

    private final ClassRoomRepository classRoomRepository;
    private final SubjectRepository subjectRepository;
    private final StaffRepository staffRepository;
    private final TimetableRepository timetableRepository;
    private final TimetableSlotRepository timetableSlotRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public HodService(ClassRoomRepository classRoomRepository,
            SubjectRepository subjectRepository,
            StaffRepository staffRepository,
            TimetableRepository timetableRepository,
            TimetableSlotRepository timetableSlotRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.classRoomRepository = classRoomRepository;
        this.subjectRepository = subjectRepository;
        this.staffRepository = staffRepository;
        this.timetableRepository = timetableRepository;
        this.timetableSlotRepository = timetableSlotRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================================
    // Helper: resolve HOD's department from their user ID
    // =========================================================================
    @Transactional(readOnly = true)
    public Department getHodDepartment(@org.springframework.lang.NonNull Long hodUserId) {
        Staff staff = staffRepository.findByUserEmail(
                userRepository.findById(hodUserId)
                        .orElseThrow(() -> new RuntimeException("User not found")).getEmail())
                .orElseThrow(() -> new RuntimeException("HOD staff profile not found"));
        if (staff.getDepartment() == null) {
            throw new RuntimeException("HOD is not assigned to any department");
        }
        return staff.getDepartment();
    }

    // =========================================================================
    // Classroom Management
    // =========================================================================

    @Transactional
    public ClassRoom createClassRoom(CreateClassRoomRequest req, @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);

        if (classRoomRepository.existsByNameAndDepartmentIdAndYearAndSemester(
                req.getName(), dept.getId(), req.getYear(), req.getSemester())) {
            throw new RuntimeException("A class with this name already exists for year " + req.getYear()
                    + " semester " + req.getSemester() + " in your department.");
        }
        if (classRoomRepository.existsByCode(req.getCode())) {
            throw new RuntimeException("Class code already in use: " + req.getCode());
        }

        ClassRoom classroom = ClassRoom.builder()
                .name(req.getName())
                .code(req.getCode().toUpperCase())
                .department(dept)
                .year(req.getYear())
                .semester(req.getSemester())
                .status(ClassRoomStatus.ACTIVE)
                .build();
        return classRoomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<ClassRoom> listClassRooms(@org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        List<ClassRoom> rooms = classRoomRepository.findByDepartmentId(dept.getId());
        // Force-initialize lazy associations
        rooms.forEach(r -> {
            if (r.getClassAdvisor() != null)
                r.getClassAdvisor().getName();
        });
        return rooms;
    }

    @Transactional(readOnly = true)
    public ClassRoom getClassRoom(@org.springframework.lang.NonNull Long classroomId,
            @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        ClassRoom room = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found: " + classroomId));
        if (!room.getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Access denied: Classroom belongs to a different department");
        }
        if (room.getClassAdvisor() != null)
            room.getClassAdvisor().getName();
        return room;
    }

    @Transactional
    public ClassRoom assignAdvisor(@org.springframework.lang.NonNull Long classroomId,
            @org.springframework.lang.NonNull Long staffId, @org.springframework.lang.NonNull Long hodUserId) {
        ClassRoom room = getClassRoom(classroomId, hodUserId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));

        // Must belong to same department
        if (staff.getDepartment() == null || !staff.getDepartment().getId().equals(room.getDepartment().getId())) {
            throw new RuntimeException("Staff does not belong to this department");
        }

        // Warn if advisor already handles 3+ active classes (but allow it)
        long activeClasses = classRoomRepository.countActiveClassesByAdvisor(staffId);
        if (activeClasses >= 3) {
            // We proceed but include warning in the response (returned via service)
            // A proper warning mechanism would use a result wrapper
        }

        room.setClassAdvisor(staff);
        return classRoomRepository.save(room);
    }

    @Transactional
    public void archiveClassRoom(@org.springframework.lang.NonNull Long classroomId,
            @org.springframework.lang.NonNull Long hodUserId) {
        ClassRoom room = getClassRoom(classroomId, hodUserId);
        room.setStatus(ClassRoomStatus.ARCHIVED);
        classRoomRepository.save(room);
    }

    // =========================================================================
    // Subject Management
    // =========================================================================

    @Transactional
    public Subject createSubject(@org.springframework.lang.NonNull Long classroomId, CreateSubjectRequest req,
            @org.springframework.lang.NonNull Long hodUserId) {
        ClassRoom room = getClassRoom(classroomId, hodUserId);

        if (subjectRepository.existsByCodeAndClassroomId(req.getCode(), classroomId)) {
            throw new RuntimeException("Subject code '" + req.getCode() + "' already exists in this class");
        }

        Subject subject = Subject.builder()
                .name(req.getName())
                .code(req.getCode().toUpperCase())
                .type(SubjectType.valueOf(req.getType()))
                .credits(req.getCredits())
                .hoursPerWeek(req.getHoursPerWeek())
                .semester(room.getSemester())
                .department(room.getDepartment())
                .classroom(room)
                .isActive(true)
                .build();
        return subjectRepository.save(subject);
    }

    @Transactional(readOnly = true)
    public List<Subject> listSubjects(@org.springframework.lang.NonNull Long classroomId,
            @org.springframework.lang.NonNull Long hodUserId) {
        getClassRoom(classroomId, hodUserId); // auth check
        List<Subject> subjects = subjectRepository.findByClassroomIdAndIsActiveTrue(classroomId);
        subjects.forEach(s -> {
            if (s.getStaff() != null)
                s.getStaff().getName();
        });
        return subjects;
    }

    @Transactional
    public Subject assignSubjectStaff(@org.springframework.lang.NonNull Long subjectId,
            @org.springframework.lang.NonNull Long staffId, @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

        // Verify subject belongs to HOD's dept
        if (!subject.getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Subject belongs to a different department");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));

        subject.setStaff(staff);
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deactivateSubject(@org.springframework.lang.NonNull Long subjectId,
            @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));
        if (!subject.getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Subject belongs to a different department");
        }
        subject.setIsActive(false);
        subjectRepository.save(subject);
    }

    // =========================================================================
    // Staff Management (HOD creates staff within their department)
    // =========================================================================

    @Transactional
    public User createStaff(CreateStaffRequest req, @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use: " + req.getEmail());
        }
        if (staffRepository.existsByEmployeeId(req.getEmployeeId())) {
            throw new RuntimeException("Employee ID already in use: " + req.getEmployeeId());
        }

        // Create user account
        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode("Welcome@123"))
                .role(UserRole.STAFF)
                .isActive(true)
                .mustChangePassword(true)
                .createdBy(hodUserId)
                .build();
        user = userRepository.save(user);

        // Create staff profile
        Staff staff = Staff.builder()
                .user(user)
                .name(req.getName())
                .employeeId(req.getEmployeeId())
                .department(dept)
                .isHod(false)
                .isActive(true)
                .build();
        staff = staffRepository.save(staff);

        // Optionally set as class advisor
        if (req.getClassAdvisorForClassId() != null) {
            ClassRoom room = classRoomRepository.findById(req.getClassAdvisorForClassId())
                    .orElseThrow(() -> new RuntimeException("Classroom not found"));
            if (!room.getDepartment().getId().equals(dept.getId())) {
                throw new RuntimeException("Classroom belongs to a different department");
            }
            room.setClassAdvisor(staff);
            classRoomRepository.save(room);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public List<Staff> listDepartmentStaff(@org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        List<Staff> staffList = staffRepository.findByDepartmentIdAndIsActiveTrue(dept.getId());
        staffList.forEach(s -> s.getUser().getEmail()); // init lazy
        return staffList;
    }

    @Transactional
    public void deactivateStaff(@org.springframework.lang.NonNull Long staffId,
            @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
        if (!staff.getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Staff belongs to a different department");
        }
        staff.setIsActive(false);
        staffRepository.save(staff);
        // Deactivate user account
        User u = staff.getUser();
        u.setIsActive(false);
        userRepository.save(u);
    }

    // =========================================================================
    // Timetable Management
    // =========================================================================

    @Transactional
    public Timetable createTimetable(@org.springframework.lang.NonNull Long classroomId, Integer semester,
            @org.springframework.lang.NonNull Long hodUserId) {
        ClassRoom room = getClassRoom(classroomId, hodUserId);

        if (timetableRepository.findByClassroomIdAndSemester(classroomId, semester).isPresent()) {
            throw new RuntimeException("A timetable already exists for semester " + semester + " in this class");
        }

        Timetable timetable = Timetable.builder()
                .classroom(room)
                .semester(semester)
                .createdBy(hodUserId)
                .build();
        return timetableRepository.save(timetable);
    }

    @Transactional(readOnly = true)
    public Timetable getTimetable(@org.springframework.lang.NonNull Long timetableId,
            @org.springframework.lang.NonNull Long hodUserId) {
        Department dept = getHodDepartment(hodUserId);
        Timetable tt = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("Timetable not found: " + timetableId));
        if (!tt.getClassroom().getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Access denied: Timetable belongs to a different department");
        }
        return tt;
    }

    @Transactional
    public TimetableSlot addTimetableSlot(@org.springframework.lang.NonNull Long timetableId,
            CreateTimetableSlotRequest req, @org.springframework.lang.NonNull Long hodUserId) {
        Timetable tt = getTimetable(timetableId, hodUserId);

        DayOfWeekEnum day = DayOfWeekEnum.valueOf(req.getDay().toUpperCase());
        SlotType slotType = SlotType.valueOf(req.getType().toUpperCase());

        // For REGULAR slots, validate subject and staff and check for conflicts
        Subject subject = null;
        Staff staff = null;

        if (slotType == SlotType.REGULAR) {
            if (req.getSubjectId() == null) {
                throw new RuntimeException("Subject is required for REGULAR slots");
            }
            subject = subjectRepository.findById(req.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + req.getSubjectId()));

            if (req.getStaffId() != null) {
                staff = staffRepository.findById(req.getStaffId())
                        .orElseThrow(() -> new RuntimeException("Staff not found: " + req.getStaffId()));

                // Check for staff conflict across all classes
                boolean hasConflict = timetableSlotRepository.existsConflict(
                        req.getStaffId(), day, req.getPeriodNumber(), timetableId);
                if (hasConflict) {
                    throw new RuntimeException("Staff '" + staff.getName()
                            + "' is already assigned to another class on " + day + " Period " + req.getPeriodNumber());
                }
            }
        }

        TimetableSlot slot = TimetableSlot.builder()
                .timetable(tt)
                .day(day)
                .periodNumber(req.getPeriodNumber())
                .startTime(LocalTime.parse(req.getStartTime()))
                .endTime(LocalTime.parse(req.getEndTime()))
                .type(slotType)
                .subject(subject)
                .staff(staff)
                .build();
        return timetableSlotRepository.save(slot);
    }

    @Transactional(readOnly = true)
    public List<TimetableSlot> getTimetableSlots(@org.springframework.lang.NonNull Long timetableId,
            @org.springframework.lang.NonNull Long hodUserId) {
        getTimetable(timetableId, hodUserId); // auth check
        List<TimetableSlot> slots = timetableSlotRepository.findByTimetableId(timetableId);
        slots.forEach(s -> {
            if (s.getSubject() != null)
                s.getSubject().getName();
            if (s.getStaff() != null)
                s.getStaff().getName();
        });
        return slots;
    }

    @Transactional
    public void deleteTimetableSlot(@org.springframework.lang.NonNull Long slotId,
            @org.springframework.lang.NonNull Long hodUserId) {
        TimetableSlot slot = timetableSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + slotId));
        getTimetable(slot.getTimetable().getId(), hodUserId); // auth check
        timetableSlotRepository.delete(slot);
    }
}
