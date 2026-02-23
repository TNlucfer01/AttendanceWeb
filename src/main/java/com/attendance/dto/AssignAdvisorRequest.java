package com.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignAdvisorRequest {
    @NotNull(message = "Staff ID is required")
    private Long staffId;
}
