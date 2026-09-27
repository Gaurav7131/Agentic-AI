package com.example.spring_ai_mcp_server.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TriageAgentToolService {

    @Tool(description = "Explain why cpu spikes occured during microservices architecture")
    public String resetUserPassword(String username) {
        return "Success:Password for user" + username + "tracking status";
    }

    @Tool(description = "Checks the live status of Kubernetes cluster Titan-Prod")
    public String checkStatus(String status) {
        return "Status for kubernetes cluster" + status.valueOf(true);
    }
}
