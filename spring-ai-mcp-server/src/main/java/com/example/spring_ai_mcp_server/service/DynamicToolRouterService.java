package com.example.spring_ai_mcp_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DynamicToolRouterService {

    private static final Logger log = LoggerFactory.getLogger(DynamicToolRouterService.class);
    private static final double ROUTING_SIMILARITY_THRESHOLD = 0.50;// Threshold

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final Map<String, ToolMetadata> toolCatalog = new HashMap<>();

    // record
    public record ToolMetadata(String name, String description, float[] vector, Object toolBean) {
    }

    // constructor
    public DynamicToolRouterService(
            ChatClient.Builder builder,
            EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.chatClient = builder.defaultSystem("""
                You are an autonomous enterprise operations assistant.
                Use the provided tools when relevant to execute user instructions safely.""").build();

        // 2) Register tools into the semantic vector catalog
        registerTool("finance", "Check account balance, ledger statements, and corporate credit lines",
                new financeTools());
        registerTool("infra", "Restart Kubernetes cluster pods, deploy nodes, and inspect container health",
                new infraTools());
        registerTool("security", "Rotate corporate IAM secrets, API gateway tokens, and database passwords",
                new securityTools());
    }

    // 1.registerTool()
    private void registerTool(String name, String descrpt, Object toolBean) {
        float[] vector = embeddingModel.embed(descrpt);
        toolCatalog.put(name, new ToolMetadata(name, descrpt, vector, toolBean));
    }

    public Map<String, String> routeAndExecute(String userPrompt) {
        float[] queryVector = embeddingModel.embed(userPrompt);

        // Score all catalog tools via cosine similarity
        ToolMetadata bestTool = null;
        double maxScore = -1.0;

        for (ToolMetadata tool : toolCatalog.values()) {
            double score = cosineSimilarity(queryVector, tool.vector());
            if (score > maxScore) {
                maxScore = score;
                bestTool = tool;
            }
        }

        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt().user(userPrompt);
        String attachedToolName = "NONE (Direct Synthesis)";

        // Pruning decision: Attach toolschema onlyif semantic-similarity meetsthreshold
        if (bestTool != null && maxScore >= ROUTING_SIMILARITY_THRESHOLD) {
            log.info("Semantic match: '{}' selected with similarity score: {}", bestTool.name(),
                    String.format("%.3f", maxScore));
            requestSpec.tools(bestTool.toolBean());
            attachedToolName = bestTool.name() + " (Score: " + String.format("%.3f", maxScore) + ")";
        } else {
            log.info("No domain tool surpassed threshold {}. Executing without schema overhead",
                    ROUTING_SIMILARITY_THRESHOLD);
        }

        String modelResponse = requestSpec.call().content();

        return Map.of(
                "query", userPrompt,
                "attachedSchema", attachedToolName,
                "response", modelResponse != null ? modelResponse : "Execution completed.");
    }

    // cosine-similarity calculations
    private double cosineSimilarity(float[] vA, float[] vB) {
        double dot = 0.0,
                nA = 0.0,
                nB = 0.0;

        for (int i = 0; i < vA.length; i++) {
            dot += vA[i] * vB[i];
            nA += Math.pow(vA[i], 2);
            nB += Math.pow(vB[i], 2);
        }
        return (nA == 0 || nB == 0) ? 0.0 : (dot / (Math.sqrt(nA) * Math.sqrt(nB)));
    }

    // 3.Tool Catalog TOP K candidates
    // A):FInanceTools
    public static class financeTools {
        @Tool(description = "Fetch current Account Balance")
        public String getBalance() {
            return "Balance: $12,450.00";
        }
    }

    // B.infraTools
    public static class infraTools {
        @Tool(description = "Restarts a production cluster pod")
        public String restartPod() {
            return "Pod restarted successfully.";
        }
    }

    // 3.SecurityTools
    public static class securityTools {
        @Tool(description = "Rotate access tokens and Credentials")
        public String rotateToken() {
            return "Access Tokens Rotated successfully.";
        }
    }
}