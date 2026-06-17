package com.worktrack.agent.controller;

import com.worktrack.agent.service.ProjectManagerAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Exposes this class as a REST endpoint container
@RequestMapping("/api/ai") // Sets the base path context
@CrossOrigin(origins = "*") // Allows future Angular communication layers to bind cleanly
public class AiAgentController {

    private final ProjectManagerAgent agent;

    // Spring automatically injects the proxy instance built from our @AiService interface definition
    public AiAgentController(ProjectManagerAgent agent) {
        this.agent = agent;
    }

    /**
     * Accepts a raw text command payload string and passes it into the LangChain4j cognitive engine loop.
     */
    @PostMapping("/command")
    public ResponseEntity<String> executeCommand(@RequestBody String prompt) {
        // Enters the conversational sliding memory matrix loop
        String aiResponse = agent.chat(prompt);

        return ResponseEntity.ok(aiResponse);
    }
}