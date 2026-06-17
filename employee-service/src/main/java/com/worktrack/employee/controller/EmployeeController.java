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
    // Spring Boot automatically hooks up our business logic service engine here
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
     * Note: value = "employeeId" explicitly bypasses standard Java reflection parameters compiler flags.
     */
    @GetMapping("/corporate/{employeeId}")
    public ResponseEntity<?> getEmployeeByCorporateId(@PathVariable(value = "employeeId") String employeeId) {
        try {
            Employee employee = employeeService.getEmployeeByCorporateId(employeeId);
            return ResponseEntity.ok(employee); // Returns HTTP Status 200 (OK)
        } catch (RuntimeException e) {
            // Catches your service layer or database query exceptions cleanly!
            System.out.println("⚠️ CONTROLLER RECOVERY -> " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"" + e.getMessage() + "\"}"); // Returns a clean 404 block instead of crashing with a 500
        }
    }
}