package com.worktrack.agent.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeServiceTools {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CircuitBreakerFactory breakerFactory;
    private final String baseEmployeeUrl;
    private final String directTaskServiceUrl;
    private final String baseLeaveUrl;

    // --- Dependency Injection via Constructor ---
    public EmployeeServiceTools(
            CircuitBreakerFactory breakerFactory,
            @Value("${BASE_EMPLOYEE_URL:http://employee-service:8081/api/employees}") String baseEmployeeUrl,
            @Value("${DIRECT_TASK_SERVICE_URL:http://task-service:8082/api/tasks}") String directTaskServiceUrl,
            @Value("${BASE_LEAVE_URL:http://leave-service:8083/api/leaves}") String baseLeaveUrl) {
        this.breakerFactory = breakerFactory;
        this.baseEmployeeUrl = baseEmployeeUrl;
        this.directTaskServiceUrl = directTaskServiceUrl;
        this.baseLeaveUrl = baseLeaveUrl;
    }

    @Tool("Retrieves all official vacation and time-off leave requests filed by an employee using their unique WorkTrack alphanumeric corporate ID string (e.g., 'WT-434019').")
    public String getLeaveRequestsByEmployeeId(String employeeId) {
        return breakerFactory.create("leaveQueryCB").run(
                () -> {
                    String targetLeaveUrl = baseLeaveUrl + "/employee/" + employeeId.trim();
                    System.out.println("🔌 CIRCUIT CLOSED (LEAVE QUERY) -> Routing safely to: " + targetLeaveUrl);
                    return restTemplate.getForObject(targetLeaveUrl, String.class);
                },
                throwable -> {
                    System.out.println("🚨 CIRCUIT TRIPPED OPEN (LEAVE QUERY) -> Downstream leave service is unreachable!");
                    return "The corporate leave registry service is temporarily offline. Vacation balances are currently unavailable.";
                }
        );
    }

    @Tool("Fetches the complete profile information of a corporate employee using their unique alphanumeric WorkTrack corporate ID string token parameter value (e.g., 'WT-434019' or 'WT-E2EB57').")
    public String getEmployeeById(String employeeId) {
        return breakerFactory.create("employeeServiceCB").run(
                () -> {
                    String finalTargetUrl = baseEmployeeUrl + "/corporate/" + employeeId.trim();
                    System.out.println("🔌 CIRCUIT CLOSED (EMPLOYEE) -> Routing safely to: " + finalTargetUrl);
                    return restTemplate.getForObject(finalTargetUrl, String.class);
                },
                throwable -> {
                    System.out.println("🚨 CIRCUIT TRIPPED OPEN (EMPLOYEE) -> Downstream employee service is unreachable!");
                    return "The corporate employee registry service is temporarily offline. Core identity profiles are currently unreachable.";
                }
        );
    }

    @Tool("Retrieves the complete list of tasks assigned to a specific employee using their unique WorkTrack alphanumeric corporate ID string (e.g., 'WT-434019').")
    public String getTasksByEmployeeId(String employeeId) {
        return breakerFactory.create("taskQueryCB").run(
                () -> {
                    String targetTaskUrl = directTaskServiceUrl + "/employee/" + employeeId.trim();
                    System.out.println("🔌 CIRCUIT CLOSED (TASK QUERY) -> Routing safely to: " + targetTaskUrl);
                    return restTemplate.getForObject(targetTaskUrl, String.class);
                },
                throwable -> {
                    System.out.println("🚨 CIRCUIT TRIPPED OPEN (TASK QUERY) -> Downstream task service is unreachable!");
                    return "I am still unable to access the task assignments for employee " + employeeId + " due to ongoing maintenance of the task registry database. Please check back later.";
                }
        );
    }

    @Tool("Creates and assigns a brand new corporate task to an employee. Returns the newly created task confirmation payload.")
    public String createNewTask(String employeeId, String title, String description, String priority) {
        return breakerFactory.create("taskCreateCB").run(
                () -> {
                    System.out.println("🤖 AI AGENT EXECUTION -> Preparing to inject a new task for worker: " + employeeId);
                    String jsonPayload = String.format(
                            "{\"employeeId\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"priority\":\"%s\",\"status\":\"TODO\"}",
                            employeeId.trim(), title.trim(), description.trim(), priority.trim()
                    );

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

                    return restTemplate.postForObject(directTaskServiceUrl, requestEntity, String.class);
                },
                throwable -> {
                    System.out.println("🚨 CIRCUIT TRIPPED OPEN (TASK CREATE) -> Provisioning engine offline!");
                    return "Failed to register new task. The underlying task creation pipeline is currently undergoing system maintenance.";
                }
        );
    }

    @Tool("Updates the progress status of an existing task (e.g., changing status to 'IN_PROGRESS' or 'COMPLETED') using its numeric database taskId variable.")
    public String updateTaskStatus(Long taskId, String newStatus) {
        return breakerFactory.create("taskPatchCB").run(
                () -> {
                    System.out.println("🤖 AI AGENT EXECUTION -> Updating lifecycle state for Task ID: " + taskId + " to " + newStatus);
                    String targetPatchUrl = directTaskServiceUrl + "/" + taskId + "/status?newStatus=" + newStatus.trim().toUpperCase();

                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                    RestTemplate patchTemplate = new RestTemplate(requestFactory);

                    return patchTemplate.patchForObject(targetPatchUrl, null, String.class);
                },
                throwable -> {
                    System.out.println("🚨 CIRCUIT TRIPPED OPEN (TASK PATCH) -> Falling back to post transmission check...");
                    try {
                        String targetPatchUrl = directTaskServiceUrl + "/" + taskId + "/status?newStatus=" + newStatus.trim().toUpperCase();
                        return restTemplate.postForObject(targetPatchUrl, null, String.class);
                    } catch (Exception ex) {
                        return "Lifecycle updates are currently locked down. The destination cluster node failed to acknowledge state transitions.";
                    }
                }
        );
    }
}