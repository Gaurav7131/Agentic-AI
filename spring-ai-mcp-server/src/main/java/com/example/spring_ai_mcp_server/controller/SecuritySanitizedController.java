package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.security.AgentSecuritySanitizerAdvisor;
import com.example.spring_ai_mcp_server.security.SecuredInfrastructureTools;

@RestController
@RequestMapping("/api/sec-agent")
public class SecuritySanitizedController {
    private static ChatClient chatclient;
    private SecuredInfrastructureTools tools;
    private AgentSecuritySanitizerAdvisor advisor;

    // constructor
    public SecuritySanitizedController(ChatClient.Builder chatBuilder, AgentSecuritySanitizerAdvisor advisor,
            SecuredInfrastructureTools tools) {
        this.tools = tools;
        this.advisor = advisor;

        this.chatclient = chatBuilder
                .defaultSystem("You are an IT Ops Infrastructure Agent. Use tools to verify service health.")
                .defaultTools(tools).defaultAdvisors(advisor).build();
    }

    @GetMapping("/diagnose")
    public Map<String, String> diagnose(
            @RequestParam(defaultValue = "Please Diagnose the Service ASAP") String prompt) {
        try {
            String result = chatclient.prompt().user(prompt).call().content();

            return Map.of("status", "Success", "result", result != null ? result : "No result Generated");
        } catch (SecurityException e) {
            return Map.of("status", "BLOCKED", "reason", e.getMessage());
        }
    }

}
