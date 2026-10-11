package com.example.spring_ai_mcp_server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.spring_ai_mcp_server.model.TaskPlanModel.ExecutionResult;
import com.example.spring_ai_mcp_server.model.TaskPlanModel.ExecutionStep;
import com.example.spring_ai_mcp_server.model.TaskPlanModel.ExeuctionPlan;

@Service
public class PlanAndSolveOrchiestator {
    private final ChatClient chatClient;
    private static final Logger log = LoggerFactory.getLogger(PlanAndSolveOrchiestator.class);
    private final CloudInfraService service;

    // constructor
    public PlanAndSolveOrchiestator(ChatClient.Builder chatBuilder, CloudInfraService infraService) {
        this.chatClient = chatBuilder.build();
        this.service = infraService;
    }

    // Step1: Plan
    public ExecutionResult planAndExecute(String goal) {
        log.info("Step 1 planning phase intiated for goal:", goal);

        // Stage 1: Generate typed, deterministic execution plan via structured output
        ExeuctionPlan plan = chatClient.prompt().user(goal).call().entity(ExeuctionPlan.class);

        if (plan != null && plan.steps() != null && plan.steps().isEmpty()) {
            return new ExecutionResult(goal, "Failed", List.of(), "Planner failed to generate tasks.");

        }

        // step 2: Execution
        log.info("Plan generated with {} steps. Initiating execution phase.", plan.steps().size());

        // rollback stack for saga compensation
        Stack<ExecutionStep> rollbackStack = new Stack<>();
        List<String> auditTrails = new ArrayList<>();

        for (ExecutionStep step : plan.steps()) {
            boolean success = service.executeStep(step);

            if (success) {
                rollbackStack.push(step);
                auditTrails.add("Step " + step.stepId() + " [" + step.actiontype() + "] completed successfully.");
            } else {
                // Stage 3: Step failed -> Trigger backward compensation
                log.error("\"Execution failed at Step {}. Initiating deterministic compensation..." + step.stepId());

                while (!rollbackStack.isEmpty()) {
                    ExecutionStep rollbackStep = rollbackStack.pop();
                    service.rollbackStep(rollbackStep);
                    auditTrails
                            .add("COMPENSATED: Rolled back " + rollbackStep.targetResource() + "," + step.actiontype());
                }
                return new ExecutionResult("plan", plan.planGoal(),
                        auditTrails,
                        "Halted at step:" + step.stepId() + "[" + step.actiontype() + "]" + "Saga rollback completed");
            }
        }
        return new ExecutionResult(plan.planGoal(), "SUCCESS", auditTrails, "All steps executed cleanly.");
    }

}
