package com.example.spring_ai_mcp_server.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResilientAgentService {

    private final ChatClient chatClient;
    private final EnterprisePaymentToolService service;

    // In-memory checkpoint store (Use PostgreSQL/Redis in production)
    private final Map<String, AgentCheckpoint> checkpointStore = new ConcurrentHashMap<>();

    public record AgentCheckpoint(String id, String userQuery, String pendingAction, double amount, String status) {
    }

    public ResilientAgentService(ChatClient.Builder builder, EnterprisePaymentToolService service) {
        this.service = service;
        this.chatClient = builder
                .defaultSystem("""
                            You are an autonomous corporate treasury agent.
                            Analyze requests carefully. For transfers, you must extract source, destination, and amount.
                        """)
                .defaultTools(service).build();
    }

    // Processed Execution
    public Map<String, Object> processUserIntent(String userMsg, double riskThreshold) {
        // regex or preliminary LLM classifier to intercept destructive amounts
        if (userMsg.contains("transfer") && userMsg.matches(".*\\b([2-9]\\d{3}|\\d{5,})\\b.*")) {
            String checkpointId = UUID.randomUUID().toString();

            AgentCheckpoint checkpoint = new AgentCheckpoint(
                    checkpointId, userMsg, "executeTransfer", 2500.0, "PENDING_APPROVAL");
            checkpointStore.put(checkpointId, checkpoint);

            return Map.of(
                    "status", "INTERRUPTED",
                    "checkpointId", checkpointId,
                    "message", "Approval Required: Action flagged as high-risk mutation. Execution paused.");
        }

        // Safe/Read-only flow continues autonomously
        String response = chatClient.prompt().user(userMsg).call().content();
        return Map.of("status", "COMPLETED", "response", response);
    }

    // Resume executions
    public Map<String, Object> resumeExecution(String checkpointId, boolean approved) {
        AgentCheckpoint checkpoint = checkpointStore.get(checkpointId);
        if (checkpoint == null) {
            throw new IllegalArgumentException("Invalid Checkpoint ID");
        }

        if (!approved) {
            checkpointStore.remove(checkpointId);
            return Map.of("status", "REJECTED", "message", "Transaction cancelled by human reviewer.");
        }

        // Resume and execute the tool deterministically
        String result = service.executetranfer("ACC-101", "ACC-999", checkpoint.amount());
        checkpointStore.remove(checkpointId);
        return Map.of("status", "COMPLETED", "result", result);
    }
}