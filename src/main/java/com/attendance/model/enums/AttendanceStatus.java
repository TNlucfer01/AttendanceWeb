package com.attendance.model.enums;

public enum AttendanceStatus {
    P,   // Present
    A,   // Absent
    IL,  // Informed Leave
    UL,  // Uninformed Leave
    OD,  // On Duty (excluded from %)
    SA,  // Staff Absent (temporary)
    SU,  // Suspended (excluded)
    NM,  // Attendance Not Marked (excluded)
    H    // Holiday (excluded)
}
