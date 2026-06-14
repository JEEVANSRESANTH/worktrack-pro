package com.worktrack.employee.repository;

import com.worktrack.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Data Access Layer for handling Employee records in PostgreSQL.
 * JpaRepository provides out-of-the-box CRUD operations (save, findById, delete).
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Custom query method: Spring parses "findByEmail" and auto-generates: SELECT * FROM employees WHERE email = ?
    Optional<Employee> findByEmail(String email);

    // Custom query method: Auto-generates: SELECT * FROM employees WHERE employee_id = ?
    Optional<Employee> findByEmployeeId(String employeeId);
}