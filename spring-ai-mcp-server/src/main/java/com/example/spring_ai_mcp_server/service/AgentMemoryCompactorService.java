package com.example.spring_ai_mcp_server.service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgentMemoryCompactorService {
    private final ChatClient chatClient;

    // In-memory key-value store
    private final Map<String, UserProfileFact> memorystore = new ConcurrentHashMap<>();

    // UserProfile record:dynamic ,system facts to batch
    public record UserProfileFact(String userId, List<String> preferences, Instant lastUpdated) {
    }

    // constructor
    public AgentMemoryCompactorService(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.defaultSystem("""
                You are an Autonomous Memory Compactor.
                    Extract durable user preferences, tech stack constraints, or security permissions
                    from the provided dialogue. Return them as a comma-separated list of facts.
                    Ignore casual greetings or temporary questions.
                """).build();
    }

    // Memory-consolidator loop
    public void compactAndStore(String userId, String userMsg, String agentResponse) {
        // prompt format
        String prompt = "Extracted permenant profile facts:\n" + userMsg + "agent response:" + agentResponse;

        String extractedFacts = chatClient.prompt().user(prompt).call().content();

        // Edge condition
        if (extractedFacts != null && !extractedFacts.isBlank()) {
            List<String> facts = Arrays.stream(extractedFacts.split(",")).map(String::trim).toList();

            memorystore.put(userId, new UserProfileFact(userId, facts, Instant.now()));
        }
    }

    // method for UserProfileFact record
    public UserProfileFact getProfile(String userId) {
        return memorystore.getOrDefault(userId, new UserProfileFact(userId, List.of(), Instant.now()));
    }

}
