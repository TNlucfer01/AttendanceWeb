package com.attendance.principal;

import com.attendance.dto.*;
import com.attendance.model.Department;
import com.attendance.model.SystemConfig;
import com.attendance.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/principal")
public class PrincipalController {

    private final PrincipalService principalService;

    public PrincipalController(PrincipalService principalService) {
        this.principalService = principalService;
    }

    // === Department CRUD ===

    @PostMapping("/departments")
    public ResponseEntity<Map<String, Object>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        Department dept = principalService.createDepartment(request);
        return ResponseEntity.ok(Map.of(
                "message", "Department created successfully",
                "departmentId", dept.getId(),
                "name", dept.getName(),
                "code", dept.getCode()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> listDepartments() {
        List<Map<String, Object>> departments = principalService.listDepartments().stream()
                .map(d -> Map.<String, Object>of(
                        "id", d.getId(),
                        "name", d.getName(),
                        "code", d.getCode(),
                        "status", d.getStatus().name(),
                        "hodName", d.getHod() != null ? d.getHod().getName() : "Unassigned"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Map<String, Object>> getDepartment(@PathVariable Long id) {
        Department d = principalService.getDepartment(id);
        return ResponseEntity.ok(Map.of(
                "id", d.getId(),
                "name", d.getName(),
                "code", d.getCode(),
                "status", d.getStatus().name(),
                "hodName", d.getHod() != null ? d.getHod().getName() : "Unassigned"));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Map<String, String>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody CreateDepartmentRequest request) {
        principalService.updateDepartment(id, request);
        return ResponseEntity.ok(Map.of("message", "Department updated successfully"));
    }

    @PutMapping("/departments/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateDepartment(@PathVariable Long id) {
        principalService.deactivateDepartment(id);
        return ResponseEntity.ok(Map.of("message", "Department deactivated"));
    }

    // === HOD Management ===

    @PutMapping("/departments/{id}/assign-hod")
    public ResponseEntity<Map<String, String>> assignHod(
            @PathVariable Long id,
            @Valid @RequestBody AssignHodRequest request) {
        principalService.assignHod(id, request.getStaffId());
        return ResponseEntity.ok(Map.of("message", "HOD assigned successfully"));
    }

    @PostMapping("/create-hod")
    public ResponseEntity<Map<String, Object>> createHod(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {
        Long principalId = (Long) httpRequest.getAttribute("userId");
        User user = principalService.createHod(request, principalId);
        return ResponseEntity.ok(Map.of(
                "message", "HOD account created and assigned to department",
                "userId", user.getId(),
                "email", user.getEmail(),
                "defaultPassword", "Welcome@123"));
    }

    // === System Config ===

    @GetMapping("/system-config")
    public ResponseEntity<SystemConfig> getSystemConfig() {
        return ResponseEntity.ok(principalService.getSystemConfig());
    }

    @PutMapping("/system-config")
    public ResponseEntity<Map<String, Object>> updateSystemConfig(
            @Valid @RequestBody UpdateSystemConfigRequest request) {
        SystemConfig config = principalService.updateSystemConfig(request);
        return ResponseEntity.ok(Map.of(
                "message", "System config updated",
                "config", config));
    }
}
