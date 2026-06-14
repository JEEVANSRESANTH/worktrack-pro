package com.worktrack.employee.service;

import com.worktrack.employee.entity.Employee;
import com.worktrack.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service // Tells Spring Boot to manage this class as our official business logic component
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // --- Dependency Injection via Constructor ---
    // Spring Boot automatically passes the active database connection repository here
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Business Rule: Registers a brand new employee in the company system.
     * Automatically generates a unique corporate Employee ID before saving.
     */
    public Employee registerEmployee(Employee employee) {
        // Check if email already exists to prevent duplicates
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new RuntimeException("Email address is already registered in WorkTrack Pro!");
        }

        // Generate a clean organizational ID string (e.g., WT-7a8b9c)
        String generatedId = "WT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        employee.setEmployeeId(generatedId);

        // Fire the repository CRUD engine to insert into PostgreSQL
        return employeeRepository.save(employee);
    }

    /**
     * Business Rule: Retrieves all active employee profiles.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Business Rule: Finds an employee by their unique organizational ID ("WT-XXXX").
     */
    public Employee getEmployeeByCorporateId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee profile not found with ID: " + employeeId));
    }
}