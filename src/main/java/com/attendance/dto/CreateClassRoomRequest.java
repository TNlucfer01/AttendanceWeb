package com.attendance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateClassRoomRequest {
    @NotBlank(message = "Class name is required")
    private String name;

    @NotBlank(message = "Class code is required")
    private String code;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    private Integer year;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;
}
