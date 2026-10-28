package com.example.spring_ai_mcp_server.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.spring_ai_mcp_server.model.AgentMeshModels.AgentActionIntent;
import com.example.spring_ai_mcp_server.model.AgentMeshModels.AgentOutboxRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AgentOutboxOrchistator {
    private static final Logger log = LoggerFactory.getLogger(AgentOutboxOrchistator.class);

    private final ChatClient chatClient;
    private final ObjectMapper mapper;

    // in-memory
    private final Map<String, AgentOutboxRecord> outboxDatabase = new ConcurrentHashMap<>();

    // constructor
    public AgentOutboxOrchistator(ChatClient.Builder chatBuilder, ObjectMapper mapper) {
        this.mapper = mapper;
        this.chatClient = chatBuilder.defaultSystem("""
                You are an autonomous Cloud Operations Planner.
                    Parse the user requirements and convert them into a typed JSON Action Intent.
                    Do not invent invalid action names. Allowed actions: SCALE_REPLICAS, MIGRATE_DATABASE.
                    Always provide a client-derived idempotencyKey """).build();
    }

    // Record:AgentOutboxRecord
    public AgentOutboxRecord planAndEmitAction(String correctionId, String userInstruction) {
        log.info("Evaluating correction plan for CorrectionId: {}" + correctionId);

        // a)Generate Typed,Structured intent via LLM
        AgentActionIntent intent = chatClient.prompt()
                .user("user instruction:" + userInstruction + "| session:" + correctionId).call()
                .entity(AgentActionIntent.class);

        if (intent == null) {
            throw new IllegalStateException("LLM failed");
        }

        try {
            String payload = mapper.writeValueAsString(intent);
            String recordId = UUID.randomUUID().toString();

            // b)Atomically persist to outbox table within standard transaction boundary
            AgentOutboxRecord record = new AgentOutboxRecord(recordId, correctionId, intent.targetAction(), payload,
                    "PENDING", Instant.now());

            outboxDatabase.put(recordId, record);
            log.info("Agent action taken successfully with:{}" + recordId);
            return record;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Failure" + e);
        }
    }

    public Map<String, AgentOutboxRecord> getOutboxEntries() {
        return Collections.unmodifiableMap(outboxDatabase);
    }

    public void markDispatched(String recordId) {
        AgentOutboxRecord rec = outboxDatabase.get(recordId);
        if (rec != null) {
            outboxDatabase.put(recordId, new AgentOutboxRecord(
                    rec.id(), rec.correctionId(), rec.eventType(), rec.payload(), "DISPATCHED", rec.createdAt()));
        }
    }
}
