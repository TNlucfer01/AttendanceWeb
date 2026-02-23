package com.attendance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSubjectRequest {
    @NotBlank(message = "Subject name is required")
    private String name;

    @NotBlank(message = "Subject code is required")
    private String code;

    @NotBlank(message = "Subject type is required (THEORY or LAB)")
    private String type; // THEORY or LAB

    @NotNull(message = "Credits are required")
    @Min(value = 0, message = "Credits cannot be negative")
    private Integer credits;

    @NotNull(message = "Hours per week is required")
    @Min(value = 1, message = "Minimum 1 hour per week")
    @Max(value = 20, message = "Maximum 20 hours per week")
    private Integer hoursPerWeek;
}
