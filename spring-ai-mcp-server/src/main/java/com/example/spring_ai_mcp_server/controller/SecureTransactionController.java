package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.SecureTransferServices;

@RestController
@RequestMapping("/api/hitl")
public class SecureTransactionController {
    private final ChatClient chatClient;
    private final SecureTransferServices services;

    public SecureTransactionController(ChatClient.Builder chatBuilder, SecureTransferServices services) {
        this.services = services;
        this.chatClient = chatBuilder.defaultSystem("""
                You are a secure banking compliance agent.
                Use your available tools to execute transfers. If a tool reports that human approval is required,
                clearly relay the reference ticket to the user.
                """).defaultTools(services).build();
    }

    @GetMapping("/chat")
    public Map<String, String> agentChat(
            @RequestParam(required = false, defaultValue = "Hello, I want to transfer funds.") String message) {
        String response = chatClient.prompt().user(message).call().content();
        return Map.of("prompt", message, "response", response != null ? response : "No response");
    }

    @PostMapping("/approve")
    public Map<String, String> approveTransaction(@RequestParam(defaultValue = "Got ticketId") String ticketId) {
        String result = services.approveAndExecute(ticketId);
        return Map.of("ticketId", ticketId, "status", result);
    }
}