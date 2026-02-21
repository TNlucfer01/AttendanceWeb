package com.attendance.admin;

import com.attendance.dto.CreatePrincipalRequest;
import com.attendance.dto.UserProfile;
import com.attendance.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create-principal")
    public ResponseEntity<Map<String, Object>> createPrincipal(
            @Valid @RequestBody CreatePrincipalRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("userId");
        User user = adminService.createPrincipal(request, adminId);
        return ResponseEntity.ok(Map.of(
                "message", "Principal account created successfully",
                "userId", user.getId(),
                "email", user.getEmail(),
                "defaultPassword", "Welcome@123"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfile>> listUsers() {
        List<UserProfile> users = adminService.listAllUsers().stream()
                .map(u -> UserProfile.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .mustChangePassword(u.getMustChangePassword())
                        .active(u.getIsActive())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully",
                "newPassword", "Reset@123"));
    }
}
