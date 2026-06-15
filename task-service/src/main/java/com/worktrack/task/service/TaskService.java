package com.worktrack.task.service;

import com.worktrack.task.entity.Task;
import com.worktrack.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Registers this class as our managed business logic component
public class TaskService {

    private final TaskRepository taskRepository;

    // --- Dependency Injection via Constructor ---
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Business Rule: Generates and assigns a brand new corporate task.
     * Enforces strict status defaults during initial database insertion.
     */
    public Task createTask(Task task) {
        // Force the initial lifecycle state to PENDING if not explicitly defined
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("PENDING");
        }

        // Save the task record securely to PostgreSQL via the JPA engine
        return taskRepository.save(task);
    }

    /**
     * Business Rule: Retrieves every single task row present in the schema.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Business Rule: Pulls a filtered list of tasks belonging to an individual worker.
     * This will act as a primary tool coordinate for our future LangChain4j agent.
     */
    public List<Task> getTasksByEmployee(String employeeId) {
        return taskRepository.findByEmployeeId(employeeId);
    }

    /**
     * Business Rule: Dynamically modifies the execution status of an active task.
     */
    public Task updateTaskStatus(Long taskId, String newStatus) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task record not found with database ID: " + taskId));

        existingTask.setStatus(newStatus.toUpperCase());
        return taskRepository.save(existingTask);
    }
}