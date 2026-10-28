<<<<<<< HEAD
package com.example.spring_ai_mcp_server.service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

=======
package com.example.agent.mesh;

import com.example.agent.mesh.AgentMeshModels.AgentActionIntent;
import com.example.agent.mesh.AgentMeshModels.ExecutionObservation;
import com.fasterxml.jackson.databind.ObjectMapper;
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import com.example.spring_ai_mcp_server.model.AgentMeshModels.AgentActionIntent;
import com.example.spring_ai_mcp_server.model.AgentMeshModels.ExecutionObservation;
import com.fasterxml.jackson.databind.ObjectMapper;
=======
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027

@Service
public class IdempotentMeshWorker {

    private static final Logger log = LoggerFactory.getLogger(IdempotentMeshWorker.class);

<<<<<<< HEAD
    private final ObjectMapper objectMapper;
    // Simulating distributed deduplication cache
    private final Set<String> processedIdempotencyKeys = ConcurrentHashMap.newKeySet();

    public IdempotentMeshWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExecutionObservation processActionMessage(String jsonPayload) {
        try {
            AgentActionIntent intent = objectMapper.readValue(jsonPayload, AgentActionIntent.class);

            // Gate: Enforce strict distributed idempotency
            if (!processedIdempotencyKeys.add(intent.idempotencyKey())) {
                log.warn("DUPLICATE DETECTED: Action with key {} already executed. Rejecting duplicate mutation.",
                        intent.idempotencyKey());
                return new ExecutionObservation(
                        intent.intentId(),
                        "SKIPPED_DUPLICATE",
                        "Idempotency token already consumed. Re-execution avoided.",
                        Instant.now());
            }

            // Execute deterministic mutation safely
            log.info("Executing isolated mutation: Action={} with params={}",
                    intent.targetAction(), intent.actionParams());

            String outcome = "SUCCESS: " + intent.targetAction() + " executed cleanly.";
            return new ExecutionObservation(intent.intentId(), "COMPLETED", outcome, Instant.now());

        } catch (Exception e) {
            log.error("Failed to parse or process intent", e);
            return new ExecutionObservation("UNKNOWN", "FAILED", e.getMessage(), Instant.now());
        }
    }
}
=======
        private final ObjectMapper objectMapper;
            // Simulating distributed deduplication cache (e.g., Redis SETNX with TTL)
                private final Set<String> processedIdempotencyKeys = ConcurrentHashMap.newKeySet();

                    public IdempotentMeshWorker(ObjectMapper objectMapper) {
                            this.objectMapper = objectMapper;
                                }

                                    public ExecutionObservation processActionMessage(String jsonPayload) {
                                            try {
                                                        AgentActionIntent intent = objectMapper.readValue(jsonPayload, AgentActionIntent.class);

                                                                    // Gate: Enforce strict distributed idempotency
                                                                                if (!processedIdempotencyKeys.add(intent.idempotencyKey())) {
                                                                                                log.warn("DUPLICATE DETECTED: Action with key {} already executed. Rejecting duplicate mutation.",
                                                                                                                        intent.idempotencyKey());
                                                                                                                                        return new ExecutionObservation(
                                                                                                                                                                intent.intentId(),
                                                                                                                                                                                        "SKIPPED_DUPLICATE",
                                                                                                                                                                                                                "Idempotency token already consumed. Re-execution avoided.",
                                                                                                                                                                                                                                        Instant.now()
                                                                                                                                                                                                                                                        );
                                                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                                                // Execute deterministic mutation safely
                                                                                                                                                                                                                                                                                            log.info("Executing isolated mutation: Action={} with params={}", 
                                                                                                                                                                                                                                                                                                                intent.targetAction(), intent.actionParameters());

                                                                                                                                                                                                                                                                                                                            String outcome = "SUCCESS: " + intent.targetAction() + " executed cleanly.";
                                                                                                                                                                                                                                                                                                                                        return new ExecutionObservation(intent.intentId(), "COMPLETED", outcome, Instant.now());

                                                                                                                                                                                                                                                                                                                                                } catch (Exception e) {
                                                                                                                                                                                                                                                                                                                                                            log.error("Failed to parse or process intent", e);
                                                                                                                                                                                                                                                                                                                                                                        return new ExecutionObservation("UNKNOWN", "FAILED", e.getMessage(), Instant.now());
                                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                                    }
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027
