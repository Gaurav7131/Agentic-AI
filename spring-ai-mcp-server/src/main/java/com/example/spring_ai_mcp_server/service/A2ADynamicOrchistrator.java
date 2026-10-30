package com.example.spring_ai_mcp_server.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.spring_ai_mcp_server.model.A2AContracts.AgentCard;
import com.example.spring_ai_mcp_server.model.A2AContracts.AgentTaskRequest;
import com.example.spring_ai_mcp_server.model.A2AContracts.AgentTaskResponse;

import jakarta.annotation.PostConstruct;

@Service
public class A2ADynamicOrchistrator {

    private final RestClient restClient;
    private final Map<String, AgentCard> discoveredAgents = new ConcurrentHashMap<>();

    @Value("${server.port:8081}")
    private String serverPort;

    public A2ADynamicOrchistrator(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    // Auto-bootstrap local agent card into memory at application startup
    @PostConstruct
    public void initDefaultAgent() {
        AgentCard localComplianceAgent = new AgentCard(
                "agent-sec-compliance-01",
                "Enterprise Security & Compliance Auditor",
                "Performs automated compliance audits on cloud deployment manifests.",
                "http://localhost:" + serverPort + "/api/a2a/compliance-agent/execute",
                List.of("INFRA_AUDIT", "PCI_DSS", "SOC2_COMPLIANCE"),
                "BEARER_TOKEN",
                Map.of("costPerInvocationTokens", 1200, "slaMs", 1500));
        discoveredAgents.put(localComplianceAgent.agentId(), localComplianceAgent);
    }

    // Dynamic discovery method for external / remote agents
    public void registerRemoteAgent(String agentCardUrl) {
        try {
            AgentCard card = restClient.get()
                    .uri(agentCardUrl)
                    .retrieve()
                    .body(AgentCard.class);

            if (card != null) {
                discoveredAgents.put(card.agentId(), card);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to discover agent at URL: " + agentCardUrl + " - " + e.getMessage());
        }
    }

    public AgentTaskResponse routeTask(String requiredCapability, String taskIntent, Map<String, Object> params) {
        AgentCard matchedAgent = discoveredAgents.values().stream()
                .filter(card -> card.capabilities() != null && card.capabilities().contains(requiredCapability))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No A2A Agent registered for capability: " + requiredCapability));

        AgentTaskRequest request = new AgentTaskRequest(
                UUID.randomUUID().toString(),
                "session-tenant-100",
                taskIntent,
                params);

        return restClient.post()
                .uri(matchedAgent.endpointUrl())
                .body(request)
                .retrieve()
                .body(AgentTaskResponse.class);
    }
}
