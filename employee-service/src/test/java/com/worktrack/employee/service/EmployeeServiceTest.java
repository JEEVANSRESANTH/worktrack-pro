package com.worktrack.employee.service;

import com.worktrack.employee.entity.Employee;
import com.worktrack.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test Suite: EmployeeService
 *
 * Purpose: Validate the EmployeeService business logic for employee registration,
 * retrieval, and uniqueness constraints using the AAA (Arrange-Act-Assert) pattern.
 *
 * Dependencies Mocked:
 * - EmployeeRepository: All database operations are mocked
 *
 * Coverage:
 * - ✅ SUCCESS: Register new employee with unique email
 * - ✅ SUCCESS: Retrieve all employees
 * - ✅ SUCCESS: Find employee by corporate ID
 * - ✅ ERROR: Reject duplicate email registration
 * - ✅ ERROR: Handle employee not found exception
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;

    // ========== ARRANGE ==========
    @BeforeEach
    void setUp() {
        // Initialize test data with typical employee values
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setEmployeeId("WT-ABC123");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@worktrack.com");
        testEmployee.setDepartment("Engineering");
        testEmployee.setRole("EMPLOYEE");
    }

    // ========== TEST 1: SUCCESS - Register new employee with unique email ==========
    @Test
    @DisplayName("Should successfully register a new employee with unique email and generate employee ID")
    void testRegisterEmployee_Success() {
        // Arrange
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Alice");
        newEmployee.setLastName("Smith");
        newEmployee.setEmail("alice.smith@worktrack.com");
        newEmployee.setDepartment("HR");
        newEmployee.setRole("EMPLOYEE");

        // Mock: Email does not exist in database
        when(employeeRepository.findByEmail(newEmployee.getEmail())).thenReturn(Optional.empty());
        // Mock: Saving the employee succeeds - return the same object that was passed in
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.registerEmployee(newEmployee);

        // Assert
        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());
        assertTrue(savedEmployee.getEmployeeId().startsWith("WT-"));
        assertEquals("Alice", savedEmployee.getFirstName());
        assertEquals("alice.smith@worktrack.com", savedEmployee.getEmail());

        // Verify the repository was called correctly
        verify(employeeRepository, times(1)).findByEmail(newEmployee.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ========== TEST 2: SUCCESS - Retrieve all employees ==========
    @Test
    @DisplayName("Should retrieve all employees from repository")
    void testGetAllEmployees_Success() {
        // Arrange
        Employee employee1 = new Employee(1L, "WT-001", "John", "Doe", "john@worktrack.com", "Engineering", "EMPLOYEE");
        Employee employee2 = new Employee(2L, "WT-002", "Jane", "Smith", "jane@worktrack.com", "HR", "MANAGER");
        List<Employee> mockEmployees = Arrays.asList(employee1, employee2);

        // Mock: Repository returns list of employees
        when(employeeRepository.findAll()).thenReturn(mockEmployees);

        // Act
        List<Employee> employees = employeeService.getAllEmployees();

        // Assert
        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Jane", employees.get(1).getFirstName());

        // Verify
        verify(employeeRepository, times(1)).findAll();
    }

    // ========== TEST 3: SUCCESS - Find employee by corporate ID ==========
    @Test
    @DisplayName("Should find employee by corporate ID and return employee details")
    void testGetEmployeeByCorporateId_Success() {
        // Arrange
        String corporateId = "WT-434019";
        Employee foundEmployee = new Employee(1L, corporateId, "John", "Doe", "john@worktrack.com", "Engineering", "EMPLOYEE");

        // Mock: Repository finds employee by corporate ID
        when(employeeRepository.findByEmployeeId(corporateId)).thenReturn(Optional.of(foundEmployee));

        // Act
        Employee employee = employeeService.getEmployeeByCorporateId(corporateId);

        // Assert
        assertNotNull(employee);
        assertEquals(corporateId, employee.getEmployeeId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());

        // Verify
        verify(employeeRepository, times(1)).findByEmployeeId(corporateId);
    }

    // ========== TEST 4: ERROR - Reject duplicate email registration ==========
    @Test
    @DisplayName("Should throw exception when attempting to register employee with duplicate email")
    void testRegisterEmployee_DuplicateEmail_Failure() {
        // Arrange
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Bob");
        newEmployee.setLastName("Wilson");
        newEmployee.setEmail("john.doe@worktrack.com"); // Same email as testEmployee
        newEmployee.setDepartment("Finance");
        newEmployee.setRole("EMPLOYEE");

        // Mock: Email already exists in database
        when(employeeRepository.findByEmail(newEmployee.getEmail())).thenReturn(Optional.of(testEmployee));

        // Act & Assert: Should throw RuntimeException with specific message
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.registerEmployee(newEmployee);
        });

        assertEquals("Email address is already registered in WorkTrack Pro!", exception.getMessage());

        // Verify: save should never be called when email is duplicate
        verify(employeeRepository, times(1)).findByEmail(newEmployee.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // ========== TEST 5: ERROR - Handle employee not found exception ==========
    @Test
    @DisplayName("Should throw exception when employee with given corporate ID does not exist")
    void testGetEmployeeByCorporateId_NotFound_Failure() {
        // Arrange
        String nonExistentId = "WT-NONEXISTENT";

        // Mock: Repository does not find employee
        when(employeeRepository.findByEmployeeId(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert: Should throw RuntimeException with specific message
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployeeByCorporateId(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Employee profile not found with ID"));
        assertTrue(exception.getMessage().contains(nonExistentId));

        // Verify
        verify(employeeRepository, times(1)).findByEmployeeId(nonExistentId);
    }

    // ========== TEST 6: Generated Employee ID format validation ==========
    @Test
    @DisplayName("Should generate employee ID in correct format (WT-XXXXXX)")
    void testRegisterEmployee_EmployeeIdFormat_Validation() {
        // Arrange
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Charlie");
        newEmployee.setLastName("Brown");
        newEmployee.setEmail("charlie.brown@worktrack.com");
        newEmployee.setDepartment("Marketing");
        newEmployee.setRole("EMPLOYEE");

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(5L); // Simulate database ID assignment
            return employee;
        });

        // Act
        Employee savedEmployee = employeeService.registerEmployee(newEmployee);

        // Assert: Verify employee ID format and uniqueness
        assertNotNull(savedEmployee.getEmployeeId());
        assertTrue(savedEmployee.getEmployeeId().matches("WT-[A-Z0-9]{6}"));
        assertTrue(savedEmployee.getEmployeeId().startsWith("WT-"));

        // Verify
        verify(employeeRepository, times(1)).findByEmail(newEmployee.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ========== TEST 7: Edge Case - Multiple employees with different emails ==========
    @Test
    @DisplayName("Should handle registering multiple employees with different emails successfully")
    void testRegisterMultipleEmployees_Success() {
        // Arrange
        Employee emp1 = new Employee();
        emp1.setEmail("emp1@worktrack.com");
        emp1.setFirstName("Employee");
        emp1.setLastName("One");
        emp1.setDepartment("IT");
        emp1.setRole("EMPLOYEE");

        Employee emp2 = new Employee();
        emp2.setEmail("emp2@worktrack.com");
        emp2.setFirstName("Employee");
        emp2.setLastName("Two");
        emp2.setDepartment("IT");
        emp2.setRole("EMPLOYEE");

        when(employeeRepository.findByEmail("emp1@worktrack.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("emp2@worktrack.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(new Employee(1L, "WT-EMP001", "Employee", "One", "emp1@worktrack.com", "IT", "EMPLOYEE"))
                .thenReturn(new Employee(2L, "WT-EMP002", "Employee", "Two", "emp2@worktrack.com", "IT", "EMPLOYEE"));

        // Act
        Employee saved1 = employeeService.registerEmployee(emp1);
        Employee saved2 = employeeService.registerEmployee(emp2);

        // Assert
        assertNotNull(saved1);
        assertNotNull(saved2);
        assertNotEquals(saved1.getEmployeeId(), saved2.getEmployeeId());

        // Verify
        verify(employeeRepository, times(2)).save(any(Employee.class));
    }
}

