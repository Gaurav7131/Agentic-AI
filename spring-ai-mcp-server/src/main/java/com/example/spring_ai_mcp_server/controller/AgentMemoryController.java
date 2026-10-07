package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.AgentMemoryCompactorService;
import com.example.spring_ai_mcp_server.service.AgentMemoryCompactorService.UserProfileFact;

@RestController
@RequestMapping("/api/agent/memory")
public class AgentMemoryController {
    private final ChatClient chatClient;
    private final AgentMemoryCompactorService service;

    // constructor
    public AgentMemoryController(ChatClient.Builder chatBuilder, AgentMemoryCompactorService service) {
        this.service = service;
        this.chatClient = chatBuilder.defaultSystem(
                "You are an enterprise AI architect assistant. Use active user profile facts to tailor answers.")
                .build();
    }

    @PostMapping("/chat")
    public Map<String, Object> executeturn(@RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "Wants default values") String msg) {

        // 1)retrieve compacted episodic(past experienced) memory
        UserProfileFact profile = service.getProfile(userId);

        // 2)injected filtered-enviction distilled facts rather than n uncomprssedtokens
        String conceptualSystemPrompt = "Known User Profile Facts:" + String.join(";", profile.preferences());

        String agentReply = chatClient.prompt().system(conceptualSystemPrompt).user(msg).call().content();

        // compact & extract durable state asynchronously
        service.compactAndStore(userId, msg, agentReply);

        return Map.of("userId", userId,
                "injectedProfilefacts", profile,
                "response:", agentReply != null ? agentReply : "No response Generated");

    }
}
