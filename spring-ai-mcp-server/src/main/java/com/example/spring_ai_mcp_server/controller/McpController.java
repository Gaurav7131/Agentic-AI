package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.EmployeeToolService;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final ChatClient chatClient;

    public McpController(ChatClient.Builder chatBuilder, EmployeeToolService service) {
        this.chatClient = chatBuilder
                .defaultSystem(
                        """
                                You are a company operations AI assistant.
                                You have access to tools that fetch internal employee departments and roles.
                                Always use the provided tools whenever a user asks about employee IDs, assignments, or departments.
                                """)
                .defaultTools(service)
                .build();
    }

    @GetMapping("/load")
    public Map<String, String> executeAgent(
            @RequestParam(defaultValue = "Which department does employee EMP-101 belong to?") String message) {

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return Map.of(
                "input", message,
                "response", response != null ? response : "");
    }
}