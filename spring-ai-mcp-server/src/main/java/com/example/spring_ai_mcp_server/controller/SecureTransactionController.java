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
        this.chatClient = chatBuilder
                .defaultSystem("""
                        You are a secure banking compliance agent.
                        Use the available transfer tool to initiate transfers.
                        If a transaction requires supervisor approval, clearly provide the Ticket ID to the user.
                        Do not fabricate successful completion if human approval is pending.
                        """)
                .defaultTools(services)
                .build();
    }

    @GetMapping("/chat")
    public Map<String, String> agentChat(
            @RequestParam(defaultValue = "Please transfer $600 from Acc-102 to Acc-101") String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return Map.of("prompt", message, "response", response != null ? response : "No response");
    }

    @PostMapping("/approve")
    public Map<String, String> approveTransaction(@RequestParam String ticketId) {
        String result = services.approveAndExecute(ticketId);
        return Map.of("ticketId", ticketId, "status", result);
    }

    @GetMapping("/balances")
    public Map<String, Object> checkBalances() {
        return Map.of("accounts", services.getBalances());
    }
}