package com.worktrack.leave.service;

import com.worktrack.leave.entity.Leave;
import com.worktrack.leave.repository.LeaveRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Registers this class as a managed business component within Spring's bean pool
public class LeaveService {

    private final LeaveRepository leaveRepository;

    // --- Dependency Injection via Constructor ---
    public LeaveService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    /**
     * Business Rule: Registers a new corporate time-off request.
     * Automatically sets initial lifecycle flags to PENDING if not specified.
     */
    public Leave requestLeave(Leave leave) {
        if (leave.getStatus() == null || leave.getStatus().isBlank()) {
            leave.setStatus("PENDING");
        }
        return leaveRepository.save(leave);
    }

    /**
     * Business Rule: Pulls a list of all registered leave requests from PostgreSQL.
     */
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    /**
     * Business Rule: Fetches leave applications filtered by an employee's string ID coordinate.
     * This serves as an operational execution handle for our LangChain4j agent.
     */
    public List<Leave> getLeavesByEmployee(String employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    /**
     * Business Rule: Updates the processing status of an active leave record.
     */
    public Leave updateLeaveStatus(Long leaveId, String newStatus) {
        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave record not found with database ID: " + leaveId));

        existingLeave.setStatus(newStatus.toUpperCase());
        return leaveRepository.save(existingLeave);
    }
}