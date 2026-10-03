package com.example.spring_ai_mcp_server.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.spring_ai_mcp_server.model.WorkFlowState;

@Service
public class AgenticWorkFlowStateService {

    private final ChatClient chatClient;
    // In-memory persistence checkpoint store (swap with Redis/PostgreSQL for
    // production)
    private final Map<String, WorkFlowState> checkpointStore = new ConcurrentHashMap<>();

    public AgenticWorkFlowStateService(ChatClient.Builder chatbuilder) {
        this.chatClient = chatbuilder
                .defaultSystem("""
                            You are a DevOps Release Evaluation Agent.
                            Evaluate incoming patch requests and rate the risk as strictly 'LOW' or 'HIGH'.
                            If the patch touches security configs or database migrations, mark it 'HIGH'.
                            Otherwise, mark it 'LOW'. Respond with only one word: LOW or HIGH.
                        """)
                .build();
    }

    // Node 1 & Edge Routing: Assess Risk/vulnerability and execute or pause
    public WorkFlowState startDeploymentWorkflow(String serviceName, String patchDetails) {
        String workflowId = UUID.randomUUID().toString().substring(0, 8);

        // Step 1: AI Node - Risk Evaluation
        String riskAssessment = chatClient.prompt().user("Patch Details:" + patchDetails).call().content().trim()
                .toUpperCase();

        // Evalaute risk
        String risk = riskAssessment.contains("HIGH") ? "HIGH" : "LOW";

        WorkFlowState state = new WorkFlowState(
                workflowId, serviceName, patchDetails, risk, "ASSESS", "Risk evaluated as: " + risk);

        // Step 2: Conditional Edge Decision(Risk==High Severity)
        if ("HIGH".equals(risk)) {
            // Checkpoint Interruption: Save state and pause
            WorkFlowState pausedState = state.withStepAndLog(
                    "APPROVAL_PENDING",
                    "Deployment paused. High-risk change requires human authorization.");
            checkpointStore.put(workflowId, pausedState);
            return pausedState;
        } else {
            // Auto-progress to deploy node
            return executeDeployment(state);
        }
    }

    // Node 2: Resume Node after Human-in-the-Loop(HITL):Supervisor(admins)decision
    public WorkFlowState resumeWorkflow(String workflowId, boolean approved) {
        WorkFlowState existingState = checkpointStore.get(workflowId);
        if (existingState == null) {
            throw new IllegalArgumentException("Workflow ID not found or already completed: " + workflowId);
        }

        if (!approved) {
            WorkFlowState rejectedState = existingState.withStepAndLog("REJECTED", "Operator denied deployment.");
            checkpointStore.remove(workflowId);
            return rejectedState;
        }

        // Resume: proceed to execute node
        WorkFlowState resumedResult = executeDeployment(existingState);
        checkpointStore.remove(workflowId);
        return resumedResult;
    }

    // Node 3: Execution Task
    private WorkFlowState executeDeployment(WorkFlowState state) {
        String log = "SUCCESS: Patch " + state.patchVersion() + " applied to " + state.serviceName();
        return state.withStepAndLog("DEPLOYED", log);
    }

    public Map<String, WorkFlowState> getPendingApprovals() {
        return checkpointStore;
    }
}