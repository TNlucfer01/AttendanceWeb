package com.attendance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTimetableSlotRequest {
    @NotBlank(message = "Day is required (MON, TUE, WED, THU, FRI, SAT)")
    private String day;

    @NotNull(message = "Period number is required")
    @Min(value = 1, message = "Period must be at least 1")
    private Integer periodNumber;

    @NotBlank(message = "Start time is required (HH:MM)")
    private String startTime; // "09:00"

    @NotBlank(message = "End time is required (HH:MM)")
    private String endTime; // "09:50"

    @NotBlank(message = "Slot type is required (REGULAR, BREAK, LUNCH)")
    private String type; // REGULAR, BREAK, LUNCH

    // Only required for REGULAR slots
    private Long subjectId;
    private Long staffId;
}
