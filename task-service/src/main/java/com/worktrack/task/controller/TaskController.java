package com.worktrack.task.controller;

import com.worktrack.task.entity.Task;
import com.worktrack.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Exposes this class as an API router that returns JSON text data
@RequestMapping("/api/tasks") // Establishes the base URL path highway for all endpoints in this file
public class TaskController {

    private final TaskService taskService;

    // --- Dependency Injection via Constructor ---
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Endpoint: HTTP POST -> http://localhost:8082/api/tasks
     * Usage: Receives a raw task payload, processes defaults, saves it, and returns the result.
     */
    @PostMapping
    public ResponseEntity<Task> createNewTask(@RequestBody Task task) {
        Task savedTask = taskService.createTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED); // Returns HTTP Status 201 (Created)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8082/api/tasks
     * Usage: Fetches a complete list of all registered tasks across the organization database.
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks); // Returns HTTP Status 200 (OK)
    }

    /**
     * Endpoint: HTTP GET -> http://localhost:8082/api/tasks/employee/{employeeId}
     * Usage: Fetches all active assignments explicitly linked to a specific worker's corporate ID.
     * Note: Explicit value assignment bypasses strict reflection compilation flags.
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Task>> getTasksByEmployee(@PathVariable(value = "employeeId") String employeeId) {
        List<Task> tasks = taskService.getTasksByEmployee(employeeId);
        return ResponseEntity.ok(tasks); // Returns HTTP Status 200 (OK)
    }

    /**
     * Endpoint: HTTP PATCH -> http://localhost:8082/api/tasks/{taskId}/status?newStatus=IN_PROGRESS
     * Usage: Allows rapid, dynamic updates to a task's internal lifecycle state.
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable(value = "taskId") Long taskId,
            @RequestParam(value = "newStatus") String newStatus) {
        Task updatedTask = taskService.updateTaskStatus(taskId, newStatus);
        return ResponseEntity.ok(updatedTask); // Returns HTTP Status 200 (OK)
    }
}