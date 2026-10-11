package com.example.spring_ai_mcp_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.model.TaskPlanModel;
import com.example.spring_ai_mcp_server.service.PlanAndSolveOrchiestator;

@RestController
@RequestMapping("/api/plan-solve")
public class PlanAndSolveController {

    private final PlanAndSolveOrchiestator orchestrator;

    // constructor
    public PlanAndSolveController(PlanAndSolveOrchiestator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/run")
    public TaskPlanModel.ExecutionResult runTask(
            @RequestParam(defaultValue = "Provision an isolated development environment with subnet, VM, and storage") String requirement) {
        return orchestrator.planAndExecute(requirement);
    }
}
