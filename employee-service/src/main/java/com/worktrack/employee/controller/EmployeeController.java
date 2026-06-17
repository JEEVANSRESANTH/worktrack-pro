package com.worktrack.employee.controller;

import com.worktrack.employee.entity.Employee;
import com.worktrack.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Tells Spring Boot that this class is an API route hub that returns JSON data
@RequestMapping("/api/employees") // Sets the base URL highway path for all endpoints in this file
public class EmployeeController {

    private final EmployeeService employeeService;

    // --- Dependency Injection via Constructor ---
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Endpoint: HTTP POST -> http://localhost:8081/api/employees
     * Usage: Receives a new employee JSON object, saves it, and returns the saved object.
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.registerEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED); // Returns HTTP Status 201 (Created)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8081/api/employees
     * Usage: Fetches a complete list of all employee profiles in the company database.
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees); // Returns HTTP Status 200 (OK)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8081/api/employees/corporate/{employeeId}
     * Usage: Fetches a single employee profile matching their unique corporate ID safely.
     * Note: Explicit value binding is kept to bypass reflection flags, but the try-catch is gone!
     */
    @GetMapping("/corporate/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByCorporateId(@PathVariable(value = "employeeId") String employeeId) {
        // Pristine production form: Business logic runs directly.
        // If an exception occurs, GlobalExceptionHandler automatically intercepts it!
        Employee employee = employeeService.getEmployeeByCorporateId(employeeId);
        return ResponseEntity.ok(employee);
    }
}