package com.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignHodRequest {
    @NotNull(message = "Staff ID is required")
    private Long staffId;
}
