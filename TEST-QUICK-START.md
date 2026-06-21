# 🚀 Quick Test Execution Guide

## One-Line Commands

### Run ALL Backend Tests
```bash
cd D:\projects\worktrack-pro && mvn clean test
```

### Run ALL Frontend Tests
```bash
cd D:\projects\worktrack-pro\worktrack-copilot && npm test -- --watch=false
```

### Run Specific Backend Test Suite

```bash
# Employee Service
mvn test -pl employee-service

# Task Service
mvn test -pl task-service

# Leave Service
mvn test -pl leave-service
```

### Run Specific Frontend Test
```bash
# CopilotService tests
npm test -- --include='**/copilot.spec.ts' --watch=false

# AppComponent tests
npm test -- --include='**/app.component.spec.ts' --watch=false
```

---

## Test Results Status

### Expected Output

#### ✅ GREEN (All Passing)
```
[INFO] BUILD SUCCESS
Total time: XX.XXXs

Chrome XX.0.XXXX (Windows 10): Executed XX of XX SUCCESS
```

#### ❌ RED (Expected Failures)
```
FAILURES

Failed test 1: testRegisterEmployee_Success
Expected: Employee ID to be generated
Actual: Employee ID is null
```

---

## Test Statistics

### Current Test Coverage

#### Backend Tests (28 total)
- **EmployeeService**: 7 tests
  - ✅ 1 test for registration success
  - ✅ 1 test for employee ID format
  - ✅ 1 test for retrieve all
  - ✅ 1 test for find by ID
  - ✅ 1 test for duplicate email error
  - ✅ 1 test for employee not found error
  - ✅ 1 test for multiple registrations

- **TaskService**: 10 tests
  - ✅ 1 test for default PENDING status
  - ✅ 1 test for explicit status override
  - ✅ 1 test for retrieve all
  - ✅ 1 test for filter by employee
  - ✅ 1 test for status update
  - ✅ 1 test for task not found error
  - ✅ 1 test for empty task list
  - ✅ 1 test for blank status default
  - ✅ 1 test for status value variations
  - ✅ 1 test for attribute preservation

- **LeaveService**: 11 tests
  - ✅ 1 test for default PENDING status
  - ✅ 1 test for explicit status override
  - ✅ 1 test for retrieve all
  - ✅ 1 test for filter by employee
  - ✅ 1 test for status update
  - ✅ 1 test for leave not found error
  - ✅ 1 test for empty leave list
  - ✅ 1 test for blank status default
  - ✅ 1 test for status value variations
  - ✅ 1 test for date preservation
  - ✅ 1 test for leave type variations

#### Frontend Tests (18 total)
- **CopilotService**: 6 tests
  - ✅ 1 test for send command success
  - ✅ 1 test for request body format
  - ✅ 1 test for HTTP 500 error
  - ✅ 1 test for HTTP 404 error
  - ✅ 1 test for empty prompt
  - ✅ 1 test for response type

- **AppComponent**: 12 tests
  - ✅ 1 test for submit and display response
  - ✅ 1 test for error handling
  - ✅ 1 test for loading state
  - ✅ 1 test for empty prompt rejection
  - ✅ 1 test for whitespace rejection
  - ✅ 1 test for duplicate submission prevention
  - ✅ 1 test for prompt trimming
  - ✅ 1 test for signal reactivity (response)
  - ✅ 1 test for signal reactivity (button text)
  - ✅ 1 test for button disabled when loading
  - ✅ 1 test for button disabled when empty
  - ✅ 1 test for Enter key submit

---

## Debugging Failed Tests

### Backend Debug Steps

1. **Check Mock Setup**
   ```java
   // Verify mock is configured correctly
   when(employeeRepository.findByEmail("test@worktrack.com"))
       .thenReturn(Optional.of(employee));
   ```

2. **Add Debug Output**
   ```java
   System.out.println("Employee ID: " + savedEmployee.getEmployeeId());
   System.out.println("Email: " + savedEmployee.getEmail());
   ```

3. **Run Single Test**
   ```bash
   mvn test -Dtest=EmployeeServiceTest#testRegisterEmployee_Success
   ```

### Frontend Debug Steps

1. **Run Tests with Browser**
   ```bash
   npm test  # This opens Karma in browser automatically
   ```

2. **Check Browser Console**
   - Open DevTools (F12)
   - Look for errors in Console tab
   - Review Network tab for HTTP requests

3. **Add Debug Logs**
   ```typescript
   console.log('Prompt:', testPrompt);
   console.log('Response:', response);
   ```

---

## Test Execution Order

### Recommended Testing Sequence

1. **Start with Backend Services (in order)**
   ```bash
   mvn test -pl employee-service   # Tests basic CRUD
   mvn test -pl task-service       # Tests status management
   mvn test -pl leave-service      # Tests date handling
   ```

2. **Then Frontend Components**
   ```bash
   npm test -- --include='**/copilot.spec.ts' --watch=false
   npm test -- --include='**/app.component.spec.ts' --watch=false
   ```

---

## Performance Tips

### Speed Up Backend Tests
- Use `skipTests` flag for builds that don't need testing
  ```bash
  mvn package -DskipTests
  ```

- Run tests in parallel (requires Maven 3.3+)
  ```bash
  mvn test -T 4  # Use 4 parallel threads
  ```

### Speed Up Frontend Tests
- Run headless (faster, no browser UI)
  ```bash
  npm test -- --watch=false --browsers=ChromeHeadless
  ```

---

## Coverage Reports

### Generate Backend Coverage
```bash
mvn clean test jacoco:report
# Open: target/site/jacoco/index.html
```

### Generate Frontend Coverage
```bash
npm test -- --code-coverage --watch=false
# Open: coverage/worktrack-copilot/index.html
```

---

## Continuous Integration

### CI Build Command
```bash
# Build + Test + Report
mvn clean verify sonar:sonar -DskipSonar=false
```

### GitHub Actions Integration
```yaml
- name: Run Backend Tests
  run: mvn clean test

- name: Run Frontend Tests
  run: |
    cd worktrack-copilot
    npm ci
    npm test -- --watch=false
```

---

## Red-Green-Refactor Workflow

### Current Phase: 🔴 RED

```bash
# 1. Run all tests (they fail as expected)
mvn test

# 2. Review failures
# 3. Implement service methods
# 4. Proceed to GREEN phase
```

### Next Phase: 🟢 GREEN

```bash
# 1. Update service implementations
# 2. Run tests (should pass)
mvn test

# 3. Verify coverage
mvn clean test jacoco:report
```

### Final Phase: 🔵 REFACTOR

```bash
# 1. Improve code without breaking tests
# 2. Run tests continuously
mvn test -Dtest=EmployeeServiceTest --watch

# 3. Verify no regressions
mvn test
```

---

## Troubleshooting

### Problem: "Tests Won't Run"

**Solution 1: Check Java Version**
```bash
java -version  # Should be 17+
mvn -version   # Should be 3.6+
```

**Solution 2: Clean Maven Cache**
```bash
mvn clean
rm -rf ~/.m2/repository/com/worktrack
mvn compile
```

### Problem: "HTTP Mock Not Working"

**Solution: Verify HttpTestingController**
```typescript
// Correct
const req = httpMock.expectOne('/api/ai/command');
req.flush(response);

// Wrong - Will hang
// Missing httpMock.expectOne()
```

### Problem: "Signal Tests Failing"

**Solution: Use Signal Calls Correctly**
```typescript
// Correct - Call signal function
expect(component.isLoading()).toBe(true);

// Wrong - Missing parentheses
expect(component.isLoading).toBe(true);
```

---

## Quick Reference Matrix

| Task | Backend | Frontend |
|------|---------|----------|
| Run all tests | `mvn test` | `npm test` |
| Run specific test | `mvn test -Dtest=ClassName` | `npm test --include='*.spec.ts'` |
| Coverage report | `mvn jacoco:report` | `npm test --code-coverage` |
| Single test method | `mvn test -Dtest=Class#method` | Filter in Karma UI |
| Continuous run | `mvn test -Dtest=Class --watch` | `npm test --watch` |
| Headless mode | N/A | `--browsers=ChromeHeadless` |
| Debug mode | Add `println` | Open DevTools (F12) |

---

## Success Indicators

### ✅ When Tests Are Ready to Pass (GREEN phase)

- [ ] All 28 backend tests compile
- [ ] All 18 frontend tests compile
- [ ] Mock setup is correct
- [ ] Service implementations align with test expectations
- [ ] No import errors

### ✅ When Tests Are Passing

- [ ] 0 compilation errors
- [ ] 0 test failures
- [ ] Line coverage ≥ 75%
- [ ] All edge cases covered

### ✅ When Ready to Deploy

- [ ] All tests pass consistently
- [ ] Code coverage ≥ 75%
- [ ] No console warnings/errors
- [ ] CI/CD pipeline green

---

**Last Updated**: June 21, 2026  
**Test Framework**: JUnit 5 | Jasmine/Karma  
**Total Tests**: 46  

