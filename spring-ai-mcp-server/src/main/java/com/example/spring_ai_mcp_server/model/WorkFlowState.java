package com.example.spring_ai_mcp_server.model;

//This class demonstated multi-stage Autonomous Deployment & Patching Agent that assesses vulnerability risk, pauses when risk is high, saves the checkpoint state, and resumes upon human verification.
public record WorkFlowState(
        String workflowId,
        String serviceName,
        String patchVersion,
        String riskLevel, // LOW, MEDIUM, HIGH
        String currentStep, // ASSESS, APPROVAL_PENDING, DEPLOYED, REJECTED
        String agentLog) {
    public WorkFlowState withStepAndLog(String nextStep, String log) {
        return new WorkFlowState(this.workflowId, this.serviceName, this.patchVersion, this.riskLevel, nextStep, log);
    }
}