package com.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateSystemConfigRequest {
    @Min(value = 1, message = "Minimum 1 period per day")
    @Max(value = 10, message = "Maximum 10 periods per day")
    private Integer periodsPerDay;

    private String workingDays; // "MON_TO_FRI" or "MON_TO_SAT"

    @Min(value = 50, message = "Threshold cannot be below 50%")
    @Max(value = 100, message = "Threshold cannot exceed 100%")
    private Double defaultAttendanceThreshold;

    @Min(value = 1, message = "Minimum 1 leave day")
    private Integer maxLeaveLimitPerSemester;

    @Min(value = 1, message = "Minimum 1 OD day")
    private Integer maxOdDaysPerSemester;
}
