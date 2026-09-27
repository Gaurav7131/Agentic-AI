package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.TriageAgentToolService;

@RestController
@RequestMapping("/api/agent")
public class TriageAgentController {

    private final ChatClient chatClient;

    // constuctor
    public TriageAgentController(ChatClient.Builder chatBuilder, TriageAgentToolService service) {
        this.chatClient = chatBuilder.defaultSystem("""
                You are an autonomous IT Triage Agent.
                     Analyze incoming user requests. If you need to reset passwords or check cluster health,
                     use your provided tools autonomously. Be concise and professional.
                """).defaultTools(service).build();
    }

    @GetMapping("/resolve")
    public Map<String, String> resolveIssue(
            @RequestParam(defaultValue = "Resolve the respective issue") String message) {
        String resolution = chatClient.prompt().user(message).call().content();

        return Map.of(
                "message", message,
                "response", resolution != null ? resolution : "No  query resolved yet");

    }
}
