package com.attendance.auth;

import com.attendance.dto.*;
import com.attendance.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequest passwordRequest) {

        Long userId = (Long) request.getAttribute("userId");
        authService.changePassword(userId, passwordRequest);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = authService.getUserById(userId);

        UserProfile profile = UserProfile.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .mustChangePassword(user.getMustChangePassword())
                .active(user.getIsActive())
                .build();

        return ResponseEntity.ok(profile);
    }
}
