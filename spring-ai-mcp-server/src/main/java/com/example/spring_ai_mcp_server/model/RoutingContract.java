package com.example.spring_ai_mcp_server.model;

public class RoutingContract {
    // Record:AgentAction schema
    public record AgentAction(
            String selectedTool,
            String payload,
            double confidenceScore,
            String reasoningTrace) {
    }

    // Record:ExecutionAction() schema
    public record ExecutionResults(
            String executedTier, // Fast_Speculative or Frontier fallback
            String executedTool,
            String output,
            long latencyMs) {
    }
}
