<<<<<<< HEAD
package com.example.spring_ai_mcp_server.controller;
=======
package com.example.agent.mesh;

import com.example.agent.mesh.AgentMeshModels.AgentOutboxRecord;
import com.example.agent.mesh.AgentMeshModels.ExecutionObservation;
import org.springframework.web.bind.annotation.*;
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027

import java.util.Map;
import java.util.UUID;

<<<<<<< HEAD
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.model.AgentMeshModels.AgentOutboxRecord;
import com.example.spring_ai_mcp_server.model.AgentMeshModels.ExecutionObservation;
import com.example.spring_ai_mcp_server.service.AgentOutboxOrchistator;
import com.example.spring_ai_mcp_server.service.IdempotentMeshWorker;

=======
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027
@RestController
@RequestMapping("/api/mesh")
public class AgentMeshController {

<<<<<<< HEAD
    private final AgentOutboxOrchistator orchestrator;
    private final IdempotentMeshWorker meshWorker;

    public AgentMeshController(AgentOutboxOrchistator orchestrator, IdempotentMeshWorker meshWorker) {
        this.orchestrator = orchestrator;
        this.meshWorker = meshWorker;
    }

    @PostMapping("/dispatch-intent")
    public Map<String, Object> dispatchIntent(@RequestParam String instruction) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        // Step 1: Write to outbox atomically
        AgentOutboxRecord outboxRecord = orchestrator.planAndEmitAction(correlationId, instruction);

        // Step 2: Simulate message bus relay to asynchronous worker
        orchestrator.markDispatched(outboxRecord.id());
        ExecutionObservation observation = meshWorker.processActionMessage(outboxRecord.payload());

        return Map.of(
                "correlationId", correlationId,
                "outboxStatus", "DISPATCHED",
                "executionResult", observation);
    }

    @PostMapping("/simulate-duplicate-replay")
    public Map<String, Object> simulateDuplicate(@RequestParam String instruction) {
        String correlationId = "replay-test-session";
        AgentOutboxRecord outboxRecord = orchestrator.planAndEmitAction(correlationId, instruction);

        // Fire First Time
        ExecutionObservation firstRun = meshWorker.processActionMessage(outboxRecord.payload());
        // Fire Second Time (Network retry / duplicate message broker redelivery)
        ExecutionObservation secondRun = meshWorker.processActionMessage(outboxRecord.payload());

        return Map.of(
                "firstInvocation", firstRun,
                "duplicateReplayInvocation", secondRun);
    }
}
=======
    private final AgentOutboxOrchestrator orchestrator;
        private final IdempotentMeshWorker meshWorker;

            public AgentMeshController(AgentOutboxOrchestrator orchestrator, IdempotentMeshWorker meshWorker) {
                    this.orchestrator = orchestrator;
                            this.meshWorker = meshWorker;
                                }

                                    @PostMapping("/dispatch-intent")
                                        public Map<String, Object> dispatchIntent(@RequestParam String instruction) {
                                                String correlationId = UUID.randomUUID().toString().substring(0, 8);

                                                        // Step 1: Write to outbox atomically
                                                                AgentOutboxRecord outboxRecord = orchestrator.planAndEmitAction(correlationId, instruction);

                                                                        // Step 2: Simulate message bus relay to asynchronous worker
                                                                                orchestrator.markDispatched(outboxRecord.id());
                                                                                        ExecutionObservation observation = meshWorker.processActionMessage(outboxRecord.payload());

                                                                                                return Map.of(
                                                                                                                "correlationId", correlationId,
                                                                                                                                "outboxStatus", "DISPATCHED",
                                                                                                                                                "executionResult", observation
                                                                                                                                                        );
                                                                                                                                                            }

                                                                                                                                                                @PostMapping("/simulate-duplicate-replay")
                                                                                                                                                                    public Map<String, Object> simulateDuplicate(@RequestParam String instruction) {
                                                                                                                                                                            String correlationId = "replay-test-session";
                                                                                                                                                                                    AgentOutboxRecord outboxRecord = orchestrator.planAndEmitAction(correlationId, instruction);

                                                                                                                                                                                            // Fire First Time
                                                                                                                                                                                                    ExecutionObservation firstRun = meshWorker.processActionMessage(outboxRecord.payload());
                                                                                                                                                                                                            // Fire Second Time (Network retry / duplicate message broker redelivery)
                                                                                                                                                                                                                    ExecutionObservation secondRun = meshWorker.processActionMessage(outboxRecord.payload());

                                                                                                                                                                                                                            return Map.of(
                                                                                                                                                                                                                                            "firstInvocation", firstRun,
                                                                                                                                                                                                                                                            "duplicateReplayInvocation", secondRun
                                                                                                                                                                                                                                                                    );
                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                        }
>>>>>>> 186d5de4ce9fc48fcaa7f23dfc66050962806027
