package com.attendance.admin;

import com.attendance.dto.CreatePrincipalRequest;
import com.attendance.model.Staff;
import com.attendance.model.User;
import com.attendance.model.enums.UserRole;
import com.attendance.repository.StaffRepository;
import com.attendance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
            StaffRepository staffRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createPrincipal(CreatePrincipalRequest request, Long createdBy) {
        // Validate: only one active Principal allowed
        if (userRepository.countByRoleAndIsActiveTrue(UserRole.PRINCIPAL) > 0) {
            throw new RuntimeException("An active Principal already exists. Deactivate the current one first.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        if (staffRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already in use: " + request.getEmployeeId());
        }

        // Create user account
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode("Welcome@123"))
                .role(UserRole.PRINCIPAL)
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
                .isActive(true)
                .build();
        staffRepository.save(staff);

        return user;
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);

        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Cannot deactivate Admin account");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long userId) {
        User user = getUserById(userId);
        user.setPasswordHash(passwordEncoder.encode("Reset@123"));
        user.setMustChangePassword(true);
        userRepository.save(user);
    }
}
