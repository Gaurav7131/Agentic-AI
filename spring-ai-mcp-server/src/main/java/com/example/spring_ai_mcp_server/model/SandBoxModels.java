package com.example.spring_ai_mcp_server.model;

import java.util.List;

public class SandBoxModels {

    public record ExecutionRequest(String strictCode, String callerContext) {
    }

    public record PolicyViolation(String rule, String reasons) {
    }

    public record ExecutionResult(
            boolean allowed,
            String output,
            List<PolicyViolation> violations,
            long executionTimeMs) {
    }
}
