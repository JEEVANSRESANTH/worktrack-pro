package com.worktrack.task.service;

import com.worktrack.task.entity.Task;
import com.worktrack.task.repository.TaskRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test Suite: TaskService
 *
 * Purpose: Validate TaskService business logic for task creation, retrieval, filtering,
 * and status updates using the AAA (Arrange-Act-Assert) pattern.
 *
 * Dependencies Mocked:
 * - TaskRepository: All database operations are mocked
 *
 * Coverage:
 * - ✅ SUCCESS: Create task with default PENDING status
 * - ✅ SUCCESS: Retrieve all tasks
 * - ✅ SUCCESS: Filter tasks by employee ID
 * - ✅ SUCCESS: Update task status
 * - ✅ ERROR: Handle task not found when updating status
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    // ========== ARRANGE ==========
    @BeforeEach
    void setUp() {
        // Initialize test data with typical task values
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Mobile App Redesign");
        testTask.setDescription("Complete redesign of the mobile application UI/UX");
        testTask.setEmployeeId("WT-434019");
        testTask.setStatus("PENDING");
        testTask.setPriority("HIGH");
    }

    // ========== TEST 1: SUCCESS - Create task with default PENDING status ==========
    @Test
    @DisplayName("Should create a new task and set status to PENDING by default")
    void testCreateTask_DefaultStatus_Success() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("New Feature Implementation");
        newTask.setDescription("Implement new feature for dashboard");
        newTask.setEmployeeId("WT-001");
        // Status is null - should be set to PENDING by service
        newTask.setPriority("MEDIUM");

        Task savedTask = new Task(
                "New Feature Implementation",
                "Implement new feature for dashboard",
                "WT-001",
                "PENDING", // Service should set this
                "MEDIUM"
        );
        savedTask.setId(2L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task createdTask = taskService.createTask(newTask);

        // Assert
        assertNotNull(createdTask);
        assertEquals("PENDING", createdTask.getStatus());
        assertEquals("New Feature Implementation", createdTask.getTitle());
        assertEquals("WT-001", createdTask.getEmployeeId());

        // Verify
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ========== TEST 2: SUCCESS - Create task with explicit status override ==========
    @Test
    @DisplayName("Should preserve explicitly set task status and not override it")
    void testCreateTask_ExplicitStatus_Success() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("Urgent Bug Fix");
        newTask.setDescription("Fix critical production bug");
        newTask.setEmployeeId("WT-002");
        newTask.setStatus("IN_PROGRESS"); // Explicitly set status
        newTask.setPriority("CRITICAL");

        Task savedTask = new Task(
                "Urgent Bug Fix",
                "Fix critical production bug",
                "WT-002",
                "IN_PROGRESS",
                "CRITICAL"
        );
        savedTask.setId(3L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task createdTask = taskService.createTask(newTask);

        // Assert
        assertNotNull(createdTask);
        assertEquals("IN_PROGRESS", createdTask.getStatus());

        // Verify
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ========== TEST 3: SUCCESS - Retrieve all tasks ==========
    @Test
    @DisplayName("Should retrieve all tasks from repository")
    void testGetAllTasks_Success() {
        // Arrange
        Task task1 = new Task("Task 1", "Description 1", "WT-001", "PENDING", "HIGH");
        task1.setId(1L);
        Task task2 = new Task("Task 2", "Description 2", "WT-002", "IN_PROGRESS", "MEDIUM");
        task2.setId(2L);
        Task task3 = new Task("Task 3", "Description 3", "WT-003", "COMPLETED", "LOW");
        task3.setId(3L);

        List<Task> mockTasks = Arrays.asList(task1, task2, task3);
        when(taskRepository.findAll()).thenReturn(mockTasks);

        // Act
        List<Task> allTasks = taskService.getAllTasks();

        // Assert
        assertNotNull(allTasks);
        assertEquals(3, allTasks.size());
        assertEquals("Task 1", allTasks.get(0).getTitle());
        assertEquals("PENDING", allTasks.get(0).getStatus());

        // Verify
        verify(taskRepository, times(1)).findAll();
    }

    // ========== TEST 4: SUCCESS - Filter tasks by employee ID ==========
    @Test
    @DisplayName("Should retrieve all tasks assigned to a specific employee")
    void testGetTasksByEmployee_Success() {
        // Arrange
        String employeeId = "WT-434019";
        Task task1 = new Task("Project Alpha", "Redesign phase 1", employeeId, "IN_PROGRESS", "HIGH");
        task1.setId(1L);
        Task task2 = new Task("Project Beta", "Development phase 2", employeeId, "PENDING", "MEDIUM");
        task2.setId(2L);

        List<Task> employeeTasks = Arrays.asList(task1, task2);
        when(taskRepository.findByEmployeeId(employeeId)).thenReturn(employeeTasks);

        // Act
        List<Task> tasks = taskService.getTasksByEmployee(employeeId);

        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getEmployeeId().equals(employeeId)));
        assertEquals("Project Alpha", tasks.get(0).getTitle());

        // Verify
        verify(taskRepository, times(1)).findByEmployeeId(employeeId);
    }

    // ========== TEST 5: SUCCESS - Update task status ==========
    @Test
    @DisplayName("Should update task status to uppercase and persist changes")
    void testUpdateTaskStatus_Success() {
        // Arrange
        Long taskId = 1L;
        String newStatus = "completed"; // lowercase - should be converted to uppercase

        Task existingTask = new Task("Task", "Description", "WT-001", "PENDING", "MEDIUM");
        existingTask.setId(taskId);

        Task updatedTask = new Task("Task", "Description", "WT-001", "COMPLETED", "MEDIUM");
        updatedTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        Task result = taskService.updateTaskStatus(taskId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus()); // Verify uppercase conversion
        assertEquals(taskId, result.getId());

        // Verify
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ========== TEST 6: ERROR - Handle task not found when updating status ==========
    @Test
    @DisplayName("Should throw exception when attempting to update status of non-existent task")
    void testUpdateTaskStatus_NotFound_Failure() {
        // Arrange
        Long nonExistentTaskId = 9999L;
        String newStatus = "COMPLETED";

        when(taskRepository.findById(nonExistentTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTaskStatus(nonExistentTaskId, newStatus);
        });

        assertTrue(exception.getMessage().contains("Task record not found with database ID"));
        assertTrue(exception.getMessage().contains(nonExistentTaskId.toString()));

        // Verify
        verify(taskRepository, times(1)).findById(nonExistentTaskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ========== TEST 7: SUCCESS - Empty task list for employee with no assignments ==========
    @Test
    @DisplayName("Should return empty list when employee has no assigned tasks")
    void testGetTasksByEmployee_EmptyList_Success() {
        // Arrange
        String employeeWithNoTasks = "WT-NOWORK";
        List<Task> emptyTaskList = Arrays.asList();

        when(taskRepository.findByEmployeeId(employeeWithNoTasks)).thenReturn(emptyTaskList);

        // Act
        List<Task> tasks = taskService.getTasksByEmployee(employeeWithNoTasks);

        // Assert
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());

        // Verify
        verify(taskRepository, times(1)).findByEmployeeId(employeeWithNoTasks);
    }

    // ========== TEST 8: Edge Case - Blank status should default to PENDING ==========
    @Test
    @DisplayName("Should set status to PENDING when status is blank string")
    void testCreateTask_BlankStatus_DefaultToPending() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("Task with blank status");
        newTask.setDescription("Description");
        newTask.setEmployeeId("WT-005");
        newTask.setStatus(""); // Blank status
        newTask.setPriority("LOW");

        Task savedTask = new Task(
                "Task with blank status",
                "Description",
                "WT-005",
                "PENDING",
                "LOW"
        );
        savedTask.setId(5L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task createdTask = taskService.createTask(newTask);

        // Assert
        assertNotNull(createdTask);
        assertEquals("PENDING", createdTask.getStatus());

        // Verify
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ========== TEST 9: Status update with various status values ==========
    @Test
    @DisplayName("Should convert various status values to uppercase during update")
    void testUpdateTaskStatus_VariousStatusValues() {
        // Arrange - Test with lowercase input
        Long taskId = 1L;
        String[] statusValues = {"pending", "in_progress", "completed", "on_hold"};

        for (String status : statusValues) {
            Task existingTask = new Task("Task", "Desc", "WT-001", "PENDING", "HIGH");
            existingTask.setId(taskId);

            Task updatedTask = new Task("Task", "Desc", "WT-001", status.toUpperCase(), "HIGH");
            updatedTask.setId(taskId);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            // Act
            Task result = taskService.updateTaskStatus(taskId, status);

            // Assert
            assertEquals(status.toUpperCase(), result.getStatus());
        }
    }

    // ========== TEST 10: Task creation preserves all attributes ==========
    @Test
    @DisplayName("Should preserve all task attributes during creation")
    void testCreateTask_PreservesAllAttributes() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("Complete Database Migration");
        newTask.setDescription("Migrate data from legacy system to PostgreSQL");
        newTask.setEmployeeId("WT-ADMIN");
        newTask.setStatus("IN_PROGRESS");
        newTask.setPriority("CRITICAL");

        Task savedTask = new Task(
                "Complete Database Migration",
                "Migrate data from legacy system to PostgreSQL",
                "WT-ADMIN",
                "IN_PROGRESS",
                "CRITICAL"
        );
        savedTask.setId(10L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task createdTask = taskService.createTask(newTask);

        // Assert
        assertEquals("Complete Database Migration", createdTask.getTitle());
        assertEquals("Migrate data from legacy system to PostgreSQL", createdTask.getDescription());
        assertEquals("WT-ADMIN", createdTask.getEmployeeId());
        assertEquals("IN_PROGRESS", createdTask.getStatus());
        assertEquals("CRITICAL", createdTask.getPriority());

        // Verify
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}

