package com.worktrack.agent.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeServiceTools {

    private final RestTemplate restTemplate = new RestTemplate();
    // Direct port target pointing to the Task Microservice engine shard
    private static final String DIRECT_TASK_SERVICE_URL = "http://localhost:8082/api/tasks";

    // Core Microservice Network Engine Coordinates on Direct Port 8081
    private static final String BASE_EMPLOYEE_URL = "http://localhost:8081/api/employees";

    @Tool("Fetches the complete profile information of a corporate employee using their unique alphanumeric WorkTrack corporate ID string token parameter value (e.g., 'WT-434019' or 'WT-E2EB57').")
    public String getEmployeeById(String employeeId) {
        try {
            // WIRING CORRECTION: Inject the explicit '/corporate/' sub-path to match EmployeeController.java
            String finalTargetUrl = BASE_EMPLOYEE_URL + "/corporate/" + employeeId.trim();
            System.out.println("🔌 PLUGGING INTO MAPPED HIGHWAY -> " + finalTargetUrl);

            String response = restTemplate.getForObject(finalTargetUrl, String.class);
            System.out.println("📬 DATA HANDSHAKE SUCCESSFUL RAW DATA RECEIVED -> " + response);
            return response;

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.out.println("❌ WORKSPACE LEDGER REJECTION -> 404 Not Found path mismatch.");
            return "Profile matching corporate ID reference code " + employeeId + " was not found in database ledger arrays.";
        } catch (Exception e) {
            System.out.println("💥 SERVICE PIPELINE FAULT -> " + e.getMessage());
            return "Network interaction timeout error occurred when pulling from microservice cluster framework layout.";
        }
    }
    @Tool("Retrieves the complete list of tasks assigned to a specific employee using their unique WorkTrack alphanumeric corporate ID string (e.g., 'WT-434019').")
    public String getTasksByEmployeeId(String employeeId) {
        try {
            // Note: Check your task-service Controller endpoints to see if it maps by query param or path variable.
            // Assuming standard path pattern format: /api/tasks/employee/{employeeId}
            String targetTaskUrl = DIRECT_TASK_SERVICE_URL + "/employee/" + employeeId.trim();
            System.out.println("🔌 PLUGGING INTO TASK HIGHWAY -> " + targetTaskUrl);

            String response = restTemplate.getForObject(targetTaskUrl, String.class);
            System.out.println("📬 TASK HANDSHAKE SUCCESSFUL RAW DATA RECEIVED -> " + response);
            return response;

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.out.println("❌ TASK REGISTRY REJECTION -> 404 Route Path Mismatch.");
            return "No active tasks found or route path is invalid for employee code " + employeeId;
        } catch (Exception e) {
            System.out.println("💥 TASK PIPELINE FAULT -> " + e.getMessage());
            return "Could not connect to task-service on port 8082.";
        }
    }
}