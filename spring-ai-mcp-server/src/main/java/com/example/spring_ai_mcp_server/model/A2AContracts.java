package com.example.spring_ai_mcp_server.model;

import java.util.List;
import java.util.Map;

public class A2AContracts {

        // Standardized Agent Card Metadata
        public record AgentCard(
                        String agentId,
                        String name,
                        String description,
                        String endpointUrl,
                        List<String> capabilities,
                        String authType,
                        Map<String, Object> metadata) {
        }

        // A2A: Task "Request" Payload
        public record AgentTaskRequest(
                        String taskId,
                        String sessionContextId,
                        String intent,
                        Map<String, Object> parameters) {
        }

        // A2A Task "Resolution" Artifact
        public record AgentTaskResponse(
                        String taskId,
                        String status, // COMPLETED, FAILED, DELEGATED
                        String assignedAgentId,
                        String resultArtifact,
                        long executionDurationMs) {
        }
}