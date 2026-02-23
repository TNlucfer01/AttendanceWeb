package com.attendance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStaffRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    // Optional: make this person a class advisor
    private Long classAdvisorForClassId;
}
