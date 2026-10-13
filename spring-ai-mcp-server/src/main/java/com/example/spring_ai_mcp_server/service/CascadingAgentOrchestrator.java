package com.example.spring_ai_mcp_server.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.spring_ai_mcp_server.model.RoutingContract.AgentAction;
import com.example.spring_ai_mcp_server.model.RoutingContract.ExecutionResults;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CascadingAgentOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(CascadingAgentOrchestrator.class);
    private static final double CONFIDENCE_SCORE = 0.81;
    private static final Set<String> ALLOWED_TOOLS = Set.of("fetchedAccountTelemetry", "remediatePodAlert",
            "escalateToHuman");

    private final ChatClient fastChatClient;// Fast speculative model
    private final ChatClient frontierChatClient;// frontier high expensive models
    private final ObjectMapper mapper;

    // constructor
    public CascadingAgentOrchestrator(@Qualifier("fastChatClient") ChatClient fastChatClient,
            @Qualifier("frontierChatClient") ChatClient frontierChatClient,
            ObjectMapper mapper) {
        this.fastChatClient = fastChatClient;
        this.frontierChatClient = frontierChatClient;
        this.mapper = mapper;
    }

    // R2:ExecutionAction
    public ExecutionResults routeAndExecute(String incidentTicket) {
        long startTime = System.currentTimeMillis();

        // Tier1 :Fast-Speculative cheap model:groq/llama
        try {
            log.info("Attempting Fast-Tier Speculative Routing...");
            // R1
            AgentAction action = evaluateAgentActionWithFastModel(incidentTicket);

            // Edge condn
            if (isValidAction(action)) {
                String toolOutput = executeTool(action.selectedTool(), action.payload());
                long latencyMs = System.currentTimeMillis() - startTime;
                return new ExecutionResults("FAST_TIER_SPECULATIVE", action.selectedTool(), toolOutput, latencyMs);
            }
            log.warn("Fast tier failed triggering frontier evalution..");
        } catch (Exception e) {
            log.warn("Fast speculative model encounted a anamoly:{}", e.getMessage());
        }

        // Tier2:Frontier high performance expensive model(Claude 3.5 sonnet,gpt 04)
        log.info("Executing Frontier model...");
        AgentAction frontier_action = evaluateAgentActionWithFrontierModel(incidentTicket);
        String tooloutput = executeTool(frontier_action.selectedTool(), frontier_action.payload());
        long latencyMs = System.currentTimeMillis() - startTime;

        return new ExecutionResults("Fast tier Frontier Model", frontier_action.selectedTool(), tooloutput, latencyMs);

    }

    // Method for Fast-Speculative Model
    private AgentAction evaluateAgentActionWithFastModel(String ticket) {

        return fastChatClient.prompt().system("""
                You are an ultra-fast triage parser. Evaluate the alert and output strict JSON matching:
                        {"selectedTool": "...", "payload": "...", "confidenceScore": 0.0-1.0, "reasoningTrace": "..."}
                        Allowed tools: fetchAccountTelemetry, remediatePodAlert, escalateToHuman.""").user(ticket)
                .call().entity(AgentAction.class);
    }

    // Method for Frontier Fallback Agent for High Computational-Intensive Payload
    private AgentAction evaluateAgentActionWithFrontierModel(String ticket) {
        return frontierChatClient.prompt()
                .system("""
                        You are a Principal Reliability AI Architect. Perform deep root-cause routing.
                        Output strict JSON matching the AgentAction schema.
                        Allowed tools: fetchAccountTelemetry, remediatePodAlert, escalateToHuman.
                        """)
                .user(ticket)
                .call()
                .entity(AgentAction.class);
    }

    // method for isValidAction validation & evaluation
    private boolean isValidAction(AgentAction action) {
        if (action == null)
            return false;
        if (!ALLOWED_TOOLS.contains(action.selectedTool()))
            return false;
        if (action.confidenceScore() < CONFIDENCE_SCORE)
            return false;
        return action.payload() != null && !action.payload().isBlank();

    }

    // method for: executeTool()
    private String executeTool(String tool, String payload) {
        return switch (tool) {
            case "fetchAccountTelemetry" -> "Telemetry metrics: CPU 42%, Memory OK for payload: " + payload;
            case "remediatePodAlert" -> "Safely initiated canary rotation on target: " + payload;
            case "escalateToHuman" -> "Dispatched priority incident escalation card with payload: " + payload;
            default -> throw new IllegalArgumentException("Unknown tool requested: " + tool);
        };
    }
}
