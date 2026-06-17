package com.worktrack.agent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.worktrack.agent.service.ProjectManagerAgent;

import java.time.Duration;

@Configuration // Registers this file as a primary structural bean definition workshop
public class AiAgentConfig {

    // Safely reads your global secure API credential key strings directly from the central Config Server environment profile
    @Value("${openai.api.key:demo}")
    private String apiKey;

    /**
     * Declares the core ChatLanguageModel routing component bean.
     * Hardwires the framework parameter boundaries to target OpenAI's robust function-calling engine tier.
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini") // Optimized framework handle targeting strict JSON tool calling
                .temperature(0.0)    // Set to 0.0 to enforce strict deterministic logic execution bounds (eliminates hallucinations)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)   // Emits complete runtime JSON interaction frames to the terminal log window for easy debugging
                .logResponses(true)
                .build();
    }

    /**
     * Instantiates a dynamic memory pool engine bean across the system network context.
     * Automatically track conversations up to a strict safe window limit of 2,000 tokens per discussion track.
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return chatMemoryId -> TokenWindowChatMemory.builder()
                .id(chatMemoryId)
                .maxTokens(2000, new OpenAiTokenizer("gpt-4o")) // Automatically ejects oldest exchange packets to prevent memory overflow
                .build();
    }
    @Bean
    public ProjectManagerAgent projectManagerAgent(
            ChatLanguageModel chatLanguageModel,
            ChatMemoryProvider chatMemoryProvider,
            com.worktrack.agent.tools.EmployeeServiceTools employeeServiceTools) { // Inject tools bean here

        return dev.langchain4j.service.AiServices.builder(ProjectManagerAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(employeeServiceTools) // Hand the tools to the agent's brain context!
                .build();
    }
}