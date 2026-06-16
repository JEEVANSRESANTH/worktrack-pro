package com.worktrack.leave.controller;

import com.worktrack.leave.entity.Leave;
import com.worktrack.leave.service.LeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Exposes this class as an API router that yields JSON output data
@RequestMapping("/api/leaves") // Standardizes the URL highway route for this entire file
public class LeaveController {

    private final LeaveService leaveService;

    // --- Dependency Injection via Constructor ---
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    /**
     * Endpoint: HTTP POST -> http://localhost:8083/api/leaves
     * Usage: Captures a raw time-off application payload, processes it, and saves it.
     */
    @PostMapping
    public ResponseEntity<Leave> createLeaveRequest(@RequestBody Leave leave) {
        Leave savedLeave = leaveService.requestLeave(leave);
        return new ResponseEntity<>(savedLeave, HttpStatus.CREATED); // Yields HTTP 201 (Created)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8083/api/leaves
     * Usage: Pulls down a global list of all recorded employee absences.
     */
    @GetMapping
    public ResponseEntity<List<Leave>> getAllLeaves() {
        List<Leave> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves); // Yields HTTP 200 (OK)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8083/api/leaves/employee/{employeeId}
     * Usage: Fetches the vacation calendars explicitly mapped to an employee string code.
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Leave>> getLeavesByEmployee(@PathVariable String employeeId) {
        List<Leave> leaves = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves); // Yields HTTP 200 (OK)
    }

    /**
     * Endpoint: HTTP PATCH -> http://localhost:8083/api/leaves/{leaveId}/status?newStatus=APPROVED
     * Usage: Enables fast state updates (Approvals/Rejections) across corporate boundaries.
     */
    @PatchMapping("/{leaveId}/status")
    public ResponseEntity<Leave> updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam String newStatus) {
        Leave updatedLeave = leaveService.updateLeaveStatus(leaveId, newStatus);
        return ResponseEntity.ok(updatedLeave); // Yields HTTP 200 (OK)
    }
}