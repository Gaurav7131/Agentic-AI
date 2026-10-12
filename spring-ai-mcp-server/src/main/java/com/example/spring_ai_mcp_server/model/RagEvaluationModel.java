package com.example.spring_ai_mcp_server.model;

public class RagEvaluationModel {
    // considence score of document relevance-evalutor
    public record DocumentGrade(boolean relevance, String reason) {
    }

    // Rewritten query
    public record RewrittenQuery(String optimizedQuery) {
    }

    // Self-corrective/adaptive rag
    public record AdaptiveRagResponse(String query, String usedQuery, int attempts, String answer, boolean verified) {
    }

}
