package com.worktrack.agent.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component // Registers this toolbelt as a discoverable Spring bean
public class EmployeeServiceTools {

    private final RestTemplate restTemplate = new RestTemplate();

    // We point directly to our API Gateway front door for structural safety
    private static final String GATEWAY_URL = "http://localhost:8080/api/employees";

    /**
     * The @Tool annotation exposes this exact Java method to the LLM.
     * The text prompt inside is translated into a strict JSON Schema description sent to OpenAI.
     */
    @Tool("Fetches the complete profile information of a corporate employee using their unique WorkTrack alpha-numeric ID string (e.g., 'WT-E101')")
    public String getEmployeeById(String employeeId) {
        try {
            System.out.println("🤖 AI AGENT EXECUTION: Calling Employee Microservice for ID -> " + employeeId);

            // Reaches straight across the cluster mesh through the gateway
            return restTemplate.getForObject(GATEWAY_URL + "/" + employeeId, String.class);
        } catch (Exception e) {
            return "Error executing tool path: Employee profile not found or service is currently unreachable.";
        }
    }
}