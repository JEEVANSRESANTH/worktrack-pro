package com.worktrack.employee.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity // Tells Spring Boot that this class maps directly to a relational database table
@Table(name = "employees") // Explicitly names the database table to avoid pluralization conflicts
@EntityListeners(AuditingEntityListener.class) // Links this class to our JPA Auditing engine
public class Employee {

    @Id // Declares this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Sets auto-incrementing IDs (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true) // Enforces string limits and database constraints
    private String employeeId; // Custom organizational ID (e.g., "WT-1001")

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String role; // Standard enterprise roles: EMPLOYEE, MANAGER, HR_ADMIN

    @CreatedDate // Auto-populates with the exact system time when an employee is first inserted
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Auto-updates whenever an employee record is modified
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // --- Enterprise Practice: Standard No-Args and All-Args Constructors ---
    public Employee() {}

    public Employee(Long id, String employeeId, String firstName, String lastName, String email, String department, String role) {
        this.id = id;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // --- Encapsulation: Explicit Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}