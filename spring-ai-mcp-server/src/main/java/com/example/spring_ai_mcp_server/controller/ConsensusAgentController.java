package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.ResilientClusterTools;

@RestController
@RequestMapping("/api/consensus")
public class ConsensusAgentController {
    private final ChatClient chatClient;

    // constructor
    public ConsensusAgentController(ChatClient.Builder chatBuilder, ResilientClusterTools tools) {
        this.chatClient = chatBuilder.defaultSystem("""
                You are an autonomous Site Reliability Engineering (SRE) Agent.
                    Safely inspect, coordinate, and execute cluster operations.
                    Always use the available tools to decommission or isolate target pods.
                """).defaultTools(tools).build();
    }

    @GetMapping("/execute")
    public Map<String, String> executeState(
            @RequestParam(defaultValue = "please decommision the pod and evaluate the response") String prompt) {
        String response = chatClient.prompt().user(prompt).call().content();

        return Map.of("prompt", prompt,
                "resolution", response != null ? response : "No response Generated");
    }

}
