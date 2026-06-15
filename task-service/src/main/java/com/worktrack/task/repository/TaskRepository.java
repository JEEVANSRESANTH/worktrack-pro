package com.worktrack.task.repository;

import com.worktrack.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Data Access Layer for handling Task records in PostgreSQL.
 * Inherits over 30 out-of-the-box database operations from JpaRepository.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Custom query method: Automatically parsed by Spring to generate:
    // SELECT * FROM tasks WHERE employee_id = ?
    List<Task> findByEmployeeId(String employeeId);
}