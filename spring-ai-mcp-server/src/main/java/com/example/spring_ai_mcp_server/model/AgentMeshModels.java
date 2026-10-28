package com.example.spring_ai_mcp_server.model;

import java.time.Instant;
import java.util.Map;

public class AgentMeshModels {
    // 1:AgentActionIntent
    public record AgentActionIntent(
            String intentId,
            String targetAction,
            Map<String, String> actionParams,
            String idempotencyKey) {
    }

    // 2:AgentOutbox
    public record AgentOutboxRecord(
            String id,
            String correctionId,
            String eventType,
            String payload,
            String status, // PENDING,DISPATCHED,PROCESSED
            Instant createdAt) {
    }

    // 3:AgentExecutionObservation
    public record ExecutionObservation(
            String intentId,
            String status,
            String executionLog,
            Instant completedAt) {
    }
}
