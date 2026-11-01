package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.component.ClusterMutationTool;

@RestController
@RequestMapping("/api/agent/distributed")
public class DistrubutedAgentController {
    private final ChatClient chatClient;

    // constructor
    public DistrubutedAgentController(ChatClient.Builder chatBuilder, ClusterMutationTool Clustertools) {
        this.chatClient = chatBuilder.defaultSystem("""
                You are an autonomous Site Reliability Engineering (SRE) Agent.
                    Inspect and safely execute cluster remediation commands.
                    Always invoke available tools to isolate or decommission target pods.
                """).defaultTools(Clustertools).build();
    }

    @GetMapping("/execute")
    public Map<String, String> executeAction(@RequestParam String prompt) {

        String response = chatClient.prompt().user(prompt).call().content();

        return Map.of("prompt", prompt,
                "Resolution:", response != null ? response : "Execution failed");
    }
}
