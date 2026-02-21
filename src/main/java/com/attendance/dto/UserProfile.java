package com.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String email;
    private String role;
    private boolean mustChangePassword;
    private boolean active;
}
