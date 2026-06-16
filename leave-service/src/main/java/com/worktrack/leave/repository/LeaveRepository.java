package com.worktrack.leave.repository;

import com.worktrack.leave.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Data Access Layer for handling Leave entries in PostgreSQL.
 * Inherits over 30 CRUD operations from JpaRepository out of the box.
 */
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Custom query method: Automatically parsed by Spring to generate:
    // SELECT * FROM leaves WHERE employee_id = ?
    List<Leave> findByEmployeeId(String employeeId);
}