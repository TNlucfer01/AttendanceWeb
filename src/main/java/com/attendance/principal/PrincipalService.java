package com.attendance.principal;

import com.attendance.dto.CreateDepartmentRequest;
import com.attendance.dto.CreateUserRequest;
import com.attendance.dto.UpdateSystemConfigRequest;
import com.attendance.model.*;
import com.attendance.model.enums.DepartmentStatus;
import com.attendance.model.enums.UserRole;
import com.attendance.model.enums.WorkingDays;
import com.attendance.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrincipalService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final PasswordEncoder passwordEncoder;

    public PrincipalService(DepartmentRepository departmentRepository,
            UserRepository userRepository,
            StaffRepository staffRepository,
            SystemConfigRepository systemConfigRepository,
            PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === Department CRUD ===

    @Transactional
    public Department createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }

        Department dept = Department.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .status(DepartmentStatus.HOD_UNASSIGNED)
                .build();
        return departmentRepository.save(dept);
    }

    @Transactional(readOnly = true)
    public List<Department> listDepartments() {
        List<Department> departments = departmentRepository.findAll();
        // Force-initialize HOD names to avoid lazy loading errors in controller
        departments.forEach(d -> {
            if (d.getHod() != null) {
                d.getHod().getName(); // trigger lazy load inside transaction
            }
        });
        return departments;
    }

    @Transactional(readOnly = true)
    public Department getDepartment(@org.springframework.lang.NonNull Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
        // Force-initialize HOD
        if (dept.getHod() != null) {
            dept.getHod().getName();
        }
        return dept;
    }

    @Transactional
    public Department updateDepartment(@org.springframework.lang.NonNull Long id, CreateDepartmentRequest request) {
        Department dept = getDepartment(id);

        // Check name uniqueness (if changed)
        if (!dept.getName().equals(request.getName()) && departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }
        if (!dept.getCode().equals(request.getCode().toUpperCase())
                && departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }

        dept.setName(request.getName());
        dept.setCode(request.getCode().toUpperCase());
        return departmentRepository.save(dept);
    }

    @Transactional
    public void deactivateDepartment(@org.springframework.lang.NonNull Long id) {
        Department dept = getDepartment(id);
        dept.setStatus(DepartmentStatus.INACTIVE);
        departmentRepository.save(dept);
    }

    // === HOD Assignment ===

    @Transactional
    public Department assignHod(@org.springframework.lang.NonNull Long departmentId,
            @org.springframework.lang.NonNull Long staffId) {
        Department dept = getDepartment(departmentId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        // Check: staff must belong to this department (or have no department yet)
        if (staff.getDepartment() != null && !staff.getDepartment().getId().equals(departmentId)) {
            throw new RuntimeException("Staff belongs to a different department: " + staff.getDepartment().getName());
        }

        // Check: staff must not already be HOD of another department
        if (staff.getIsHod() && dept.getHod() != null && !dept.getHod().getId().equals(staffId)) {
            throw new RuntimeException("Staff is already HOD of another department");
        }

        // Remove previous HOD if exists
        if (dept.getHod() != null) {
            Staff previousHod = dept.getHod();
            previousHod.setIsHod(false);
            // Update the previous HOD's user role back to STAFF
            User previousUser = previousHod.getUser();
            previousUser.setRole(UserRole.STAFF);
            userRepository.save(previousUser);
            staffRepository.save(previousHod);
        }

        // Assign new HOD
        staff.setIsHod(true);
        staff.setDepartment(dept);
        staffRepository.save(staff);

        // Update user role to HOD
        User user = staff.getUser();
        user.setRole(UserRole.HOD);
        userRepository.save(user);

        dept.setHod(staff);
        dept.setStatus(DepartmentStatus.ACTIVE);
        return departmentRepository.save(dept);
    }

    // === HOD Account Creation ===

    @Transactional
    public User createHod(CreateUserRequest request, @org.springframework.lang.NonNull Long createdBy) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }
        if (staffRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already in use: " + request.getEmployeeId());
        }

        Department dept = getDepartment(request.getDepartmentId());

        // Create user account with HOD role
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode("Welcome@123"))
                .role(UserRole.HOD)
                .isActive(true)
                .mustChangePassword(true)
                .createdBy(createdBy)
                .build();
        user = userRepository.save(user);

        // Create staff profile
        Staff staff = Staff.builder()
                .user(user)
                .name(request.getName())
                .employeeId(request.getEmployeeId())
                .department(dept)
                .isHod(true)
                .isActive(true)
                .build();
        staffRepository.save(staff);

        // Update department
        dept.setHod(staff);
        dept.setStatus(DepartmentStatus.ACTIVE);
        departmentRepository.save(dept);

        return user;
    }

    // === System Config ===

    public SystemConfig getSystemConfig() {
        return systemConfigRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("System config not initialized"));
    }

    @Transactional
    public SystemConfig updateSystemConfig(UpdateSystemConfigRequest request) {
        SystemConfig config = getSystemConfig();

        if (request.getPeriodsPerDay() != null) {
            config.setPeriodsPerDay(request.getPeriodsPerDay());
        }
        if (request.getWorkingDays() != null) {
            config.setWorkingDays(WorkingDays.valueOf(request.getWorkingDays()));
        }
        if (request.getDefaultAttendanceThreshold() != null) {
            config.setDefaultAttendanceThreshold(request.getDefaultAttendanceThreshold());
        }
        if (request.getMaxLeaveLimitPerSemester() != null) {
            config.setMaxLeaveLimitPerSemester(request.getMaxLeaveLimitPerSemester());
        }
        if (request.getMaxOdDaysPerSemester() != null) {
            config.setMaxOdDaysPerSemester(request.getMaxOdDaysPerSemester());
        }

        return systemConfigRepository.save(config);
    }
}
