package com.example.spring_ai_mcp_server.model;

import java.util.List;

public class TaskPlanModel {
    // Execution Step record
    public record ExecutionStep(int stepId, String taskDesc, String actiontype, String targetResource) {
    }

    // Execution Plan
    public record ExeuctionPlan(String planGoal, List<ExecutionStep> steps) {
    }

    // Execution Result
    public record ExecutionResult(String planGoal, String status, List<String> completedSteps, String failureReason) {
    }

}
