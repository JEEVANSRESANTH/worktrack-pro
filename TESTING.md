# 🧪 Comprehensive Testing Suite - WorkTrack Pro

## Overview

This document outlines the complete testing suite for the **WorkTrack Pro** full-stack application, covering both **Backend (Java/Spring Boot)** and **Frontend (Angular 18)** components.

---

## Table of Contents

1. [Testing Architecture](#testing-architecture)
2. [Backend Unit Tests](#backend-unit-tests)
3. [Frontend Unit Tests](#frontend-unit-tests)
4. [Running Tests](#running-tests)
5. [Code Coverage](#code-coverage)
6. [Test Execution Guide](#test-execution-guide)
7. [Red-Green-Refactor Cycle](#red-green-refactor-cycle)

---

## Testing Architecture

### Tech Stack

#### Backend Testing
- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito 5.x
- **Build Tool**: Maven 3.x
- **Pattern**: AAA (Arrange-Act-Assert)

#### Frontend Testing
- **Framework**: Jasmine
- **Test Runner**: Karma
- **HTTP Mocking**: HttpClientTestingModule
- **Build Tool**: Angular CLI 18

### Dependencies Added

**Parent POM (`pom.xml`)** now includes:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Backend Unit Tests

### 1. EmployeeService Tests
**Location**: `employee-service/src/test/java/com/worktrack/employee/service/EmployeeServiceTest.java`

#### Test Coverage (7 tests)

| Test ID | Test Name | Type | Status |
|---------|-----------|------|--------|
| 1 | Register employee with unique email | ✅ Success | Failing (Red) |
| 2 | Generate employee ID in WT-XXXXXX format | ✅ Success | Failing (Red) |
| 3 | Retrieve all employees | ✅ Success | Failing (Red) |
| 4 | Find employee by corporate ID | ✅ Success | Failing (Red) |
| 5 | Reject duplicate email registration | ❌ Error | Failing (Red) |
| 6 | Handle employee not found | ❌ Error | Failing (Red) |
| 7 | Register multiple employees | ✅ Success | Failing (Red) |

#### Dependencies Mocked
- `EmployeeRepository.findByEmail()` → Controls email duplicate checking
- `EmployeeRepository.findByEmployeeId()` → Controls corporate ID lookups
- `EmployeeRepository.save()` → Controls employee persistence
- `EmployeeRepository.findAll()` → Controls full list retrieval

#### Key Test Scenarios

**Success Path 1: Register New Employee**
```
Given: A new employee with unique email
When: registerEmployee() is called
Then: Employee ID is generated (WT-XXXXXX) and saved
```

**Error Path 1: Duplicate Email**
```
Given: Email already exists in database
When: registerEmployee() is called with duplicate email
Then: RuntimeException thrown with message
```

---

### 2. TaskService Tests
**Location**: `task-service/src/test/java/com/worktrack/task/service/TaskServiceTest.java`

#### Test Coverage (10 tests)

| Test ID | Test Name | Type | Status |
|---------|-----------|------|--------|
| 1 | Create task with default PENDING status | ✅ Success | Failing (Red) |
| 2 | Create task with explicit status override | ✅ Success | Failing (Red) |
| 3 | Retrieve all tasks | ✅ Success | Failing (Red) |
| 4 | Filter tasks by employee ID | ✅ Success | Failing (Red) |
| 5 | Update task status | ✅ Success | Failing (Red) |
| 6 | Handle task not found during update | ❌ Error | Failing (Red) |
| 7 | Empty task list for employee | ✅ Success | Failing (Red) |
| 8 | Blank status defaults to PENDING | ✅ Success | Failing (Red) |
| 9 | Various status value conversions | ✅ Success | Failing (Red) |
| 10 | Preserve all task attributes | ✅ Success | Failing (Red) |

#### Dependencies Mocked
- `TaskRepository.findAll()` → Full task list
- `TaskRepository.findByEmployeeId()` → Employee-specific tasks
- `TaskRepository.findById()` → Single task lookup
- `TaskRepository.save()` → Task persistence

#### Key Test Scenarios

**Success Path 1: Create Task with Default Status**
```
Given: A new task with null status
When: createTask() is called
Then: Status is set to "PENDING" and saved
```

**Success Path 2: Filter Tasks by Employee**
```
Given: An employee ID "WT-434019"
When: getTasksByEmployee("WT-434019") is called
Then: All tasks for that employee are returned
```

---

### 3. LeaveService Tests
**Location**: `leave-service/src/test/java/com/worktrack/leave/service/LeaveServiceTest.java`

#### Test Coverage (11 tests)

| Test ID | Test Name | Type | Status |
|---------|-----------|------|--------|
| 1 | Request leave with default PENDING status | ✅ Success | Failing (Red) |
| 2 | Request leave with explicit status | ✅ Success | Failing (Red) |
| 3 | Retrieve all leave requests | ✅ Success | Failing (Red) |
| 4 | Filter leaves by employee ID | ✅ Success | Failing (Red) |
| 5 | Update leave status | ✅ Success | Failing (Red) |
| 6 | Handle leave not found during update | ❌ Error | Failing (Red) |
| 7 | Empty leave list for employee | ✅ Success | Failing (Red) |
| 8 | Blank status defaults to PENDING | ✅ Success | Failing (Red) |
| 9 | Various status value conversions | ✅ Success | Failing (Red) |
| 10 | Preserve date information | ✅ Success | Failing (Red) |
| 11 | Preserve leave types (CASUAL, SICK, etc.) | ✅ Success | Failing (Red) |

#### Dependencies Mocked
- `LeaveRepository.findAll()` → All leave requests
- `LeaveRepository.findByEmployeeId()` → Employee-specific leaves
- `LeaveRepository.findById()` → Single leave lookup
- `LeaveRepository.save()` → Leave request persistence

---

## Frontend Unit Tests

### 1. CopilotService Tests
**Location**: `worktrack-copilot/src/app/services/copilot.spec.ts`

#### Test Coverage (6 tests)

| Test ID | Test Name | Type | Status |
|---------|-----------|------|--------|
| 1 | Send command and return AI response | ✅ Success | Failing (Red) |
| 2 | Request body is raw text (not JSON) | ✅ Success | Failing (Red) |
| 3 | Propagate HTTP 500 error | ❌ Error | Failing (Red) |
| 4 | Propagate HTTP 404 error | ❌ Error | Failing (Red) |
| 5 | Handle empty string prompt | ✅ Success | Failing (Red) |
| 6 | Verify response type is "text" | ✅ Success | Failing (Red) |

#### Dependencies Mocked
- `HttpClient.post()` → HTTP communication (using `HttpClientTestingModule`)

#### Key Test Scenarios

**Success Path 1: Send Command to AI Agent**
```
Given: Prompt "What is employee WT-434019 working on?"
When: sendCommand(prompt) is called
Then: Observable resolves with AI response string
```

**Contract Validation: Request Body Format**
```
Given: A text prompt
When: sendCommand(prompt) is called
Then: Request body is raw text, NOT { prompt: ... } JSON object
```

---

### 2. AppComponent Tests
**Location**: `worktrack-copilot/src/app/app.component.spec.ts`

#### Test Coverage (12 tests)

| Test ID | Test Name | Type | Status |
|---------|-----------|------|--------|
| 1 | Submit prompt and update response signal | ✅ Success | Failing (Red) |
| 2 | Handle service error gracefully | ❌ Error | Failing (Red) |
| 3 | Set isLoading during request | ✅ Success | Failing (Red) |
| 4 | Reject empty prompt | ✅ Success | Failing (Red) |
| 5 | Reject whitespace-only prompt | ✅ Success | Failing (Red) |
| 6 | Prevent duplicate submissions while loading | ✅ Success | Failing (Red) |
| 7 | Trim whitespace from prompt | ✅ Success | Failing (Red) |
| 8 | Signal reactivity - aiResponse updates template | ✅ Success | Failing (Red) |
| 9 | Signal reactivity - isLoading changes button text | ✅ Success | Failing (Red) |
| 10 | Disable button when isLoading | ✅ Success | Failing (Red) |
| 11 | Disable button when prompt empty | ✅ Success | Failing (Red) |
| 12 | Submit on Enter key press | ✅ Success | Failing (Red) |

#### Dependencies Mocked
- `CopilotService.sendCommand()` → Service calls (using Jasmine spies)

#### Key Test Scenarios

**Success Path 1: Submit Prompt and Display Response**
```
Given: User types prompt and clicks Send
When: submitCommand() completes successfully
Then: 
  - isLoading signal goes true → false
  - aiResponse signal displays result
  - prompt input is cleared
```

**Error Path 1: API Failure**
```
Given: Backend service is unavailable
When: submitCommand() receives error
Then: Error message displayed and loading state cleared
```

**Input Validation: Whitespace Rejection**
```
Given: Prompt is "   " (whitespace only)
When: submitCommand() is called
Then: Service.sendCommand is never called
```

---

## Running Tests

### Backend Tests

#### Run All Backend Tests
```bash
cd D:\projects\worktrack-pro
mvn test
```

#### Run Tests for Specific Service
```bash
# Employee Service Tests
mvn test -pl employee-service

# Task Service Tests
mvn test -pl task-service

# Leave Service Tests
mvn test -pl leave-service
```

#### Run a Specific Test Class
```bash
mvn test -Dtest=EmployeeServiceTest
mvn test -Dtest=TaskServiceTest
mvn test -Dtest=LeaveServiceTest
```

#### Run a Specific Test Method
```bash
mvn test -Dtest=EmployeeServiceTest#testRegisterEmployee_Success
```

---

### Frontend Tests

#### Run All Frontend Tests (Karma)
```bash
cd worktrack-copilot
npm test
```

#### Run Tests for Specific Service
```bash
npm test -- --include='**/copilot.spec.ts'
```

#### Run Tests for Component
```bash
npm test -- --include='**/app.component.spec.ts'
```

#### Run Tests in Headless Mode (CI/CD)
```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

#### Generate Coverage Report (Frontend)
```bash
npm test -- --code-coverage
# Report available in: coverage/worktrack-copilot/index.html
```

---

## Code Coverage

### Backend Coverage Goals
- **Line Coverage**: ≥ 80%
- **Branch Coverage**: ≥ 75%
- **Method Coverage**: ≥ 85%

### Generate Backend Coverage Report
```bash
cd D:\projects\worktrack-pro
mvn clean test jacoco:report
# Report available in: target/site/jacoco/index.html
```

### Frontend Coverage Goals
- **Line Coverage**: ≥ 75%
- **Branch Coverage**: ≥ 70%
- **Method Coverage**: ≥ 80%

### View Frontend Coverage
```bash
cd worktrack-copilot
npm test -- --code-coverage --watch=false
# Report available in: coverage/worktrack-copilot/index.html
```

---

## Test Execution Guide

### Phase 1: RED (Failing Tests)

All tests currently fail because the implementation is incomplete or needs verification:

```bash
# Backend: Run and observe failures
mvn test

# Frontend: Run and observe failures
npm test
```

**Expected Output**: Tests show failures due to:
- ✅ Correct test assertions
- ✅ Proper mocking setup
- ❌ Implementation needed (Green phase)

### Phase 2: GREEN (Make Tests Pass)

For backend services, update the `service` implementations to match test expectations:

**Example - EmployeeService.registerEmployee():**
```java
public Employee registerEmployee(Employee employee) {
    // Check if email exists (mocked in tests)
    if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
        throw new RuntimeException("Email address is already registered in WorkTrack Pro!");
    }
    // Generate ID in WT-XXXXXX format
    String generatedId = "WT-" + UUID.randomUUID().toString()
        .substring(0, 6).toUpperCase();
    employee.setEmployeeId(generatedId);
    
    // Save and return
    return employeeRepository.save(employee);
}
```

After updating implementations:

```bash
# Run tests again - they should pass
mvn test
```

### Phase 3: REFACTOR (Improve Code Quality)

Once tests pass, refactor without breaking functionality:

```bash
# Run tests continuously during refactoring
mvn test -Dtest=EmployeeServiceTest --watch

# Frontend refactoring with hot reload
npm test -- --watch=true
```

---

## AAA Pattern Reference

All tests follow the **Arrange-Act-Assert** pattern:

### Template

```java
@Test
@DisplayName("Should [desired behavior]")
void testSomething_Scenario_Expected() {
    // ========== ARRANGE ==========
    // Setup test data and mock dependencies
    Employee testEmployee = new Employee();
    testEmployee.setEmail("test@worktrack.com");
    when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // ========== ACT ==========
    // Execute the method under test
    Employee savedEmployee = employeeService.registerEmployee(testEmployee);

    // ========== ASSERT ==========
    // Verify the results match expectations
    assertNotNull(savedEmployee);
    assertNotNull(savedEmployee.getEmployeeId());
    
    // Verify mocks were called correctly
    verify(employeeRepository, times(1)).save(any(Employee.class));
}
```

---

## Mocking Best Practices

### Backend - Mockito Patterns

#### Mock a Repository Method
```java
@Mock
private EmployeeRepository employeeRepository;

// In test:
when(employeeRepository.findByEmail("test@worktrack.com"))
    .thenReturn(Optional.of(employee));
```

#### Verify Mock Interactions
```java
// Verify called exactly once
verify(employeeRepository, times(1)).save(any(Employee.class));

// Verify never called
verify(employeeRepository, never()).delete(any());

// Verify called at least once
verify(employeeRepository, atLeastOnce()).findAll();
```

---

### Frontend - HttpClientTestingModule

#### Mock HTTP Request
```typescript
const req = httpMock.expectOne('/api/ai/command');
expect(req.request.method).toBe('POST');
expect(req.request.body).toBe(prompt);
req.flush('Mock response');
```

#### Mock HTTP Error
```typescript
const req = httpMock.expectOne('/api/ai/command');
req.flush('Error message', { status: 500, statusText: 'Server Error' });
```

---

## Summary

### Tests Created

#### Backend
- **EmployeeServiceTest**: 7 test methods, 340+ lines
- **TaskServiceTest**: 10 test methods, 400+ lines
- **LeaveServiceTest**: 11 test methods, 420+ lines
- **Total Backend Tests**: 28 test cases

#### Frontend
- **CopilotService Tests**: 6 test methods, 180+ lines
- **AppComponent Tests**: 12 test methods, 300+ lines
- **Total Frontend Tests**: 18 test cases

### Total: **46 Test Cases** across full stack

---

## Next Steps

1. ✅ **Step 1 Complete**: Test skeletons generated with proper AAA pattern
2. ✅ **Step 2 Complete**: All tests in RED state (failing as expected)
3. ⏭️ **Step 3**: Implement service methods to make tests pass (GREEN)
4. ⏭️ **Step 4**: Refactor code for quality while maintaining test success (REFACTOR)

### To Begin Implementation

Provide any service implementation that needs updates, or verify that:
- All existing services match the test expectations
- Database repositories work as mocked
- HTTP communication follows the tested contracts

---

## CI/CD Integration

### GitHub Actions / Jenkins Example

```yaml
name: Test Suite
on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn clean test

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '18'
      - run: cd worktrack-copilot && npm install && npm test -- --watch=false
```

---

## Support & Questions

For test-related questions or issues:
1. Review the test class documentation comments
2. Check the mock setup in `@BeforeEach` methods
3. Verify entity constructors match test data setup
4. Ensure service methods handle all mocked scenarios

---

**Test Suite Version**: 1.0.0  
**Created**: June 21, 2026  
**Framework**: JUnit 5 + Jasmine | Mocking: Mockito + HttpClientTestingModule

