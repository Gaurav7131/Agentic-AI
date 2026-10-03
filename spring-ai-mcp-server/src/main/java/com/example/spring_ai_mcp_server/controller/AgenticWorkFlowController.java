package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.model.WorkFlowState;
import com.example.spring_ai_mcp_server.service.AgenticWorkFlowStateService;

@RestController
@RequestMapping("/api/workflow")
public class AgenticWorkFlowController {
    private final AgenticWorkFlowStateService service;

    // constructor
    public AgenticWorkFlowController(AgenticWorkFlowStateService service) {
        this.service = service;
    }

    // Deploy
    @PostMapping("/deploy")
    public WorkFlowState intiatePipeline(@RequestParam String servicename,
            @RequestParam String patchDetails) {
        return service.startDeploymentWorkflow(servicename, patchDetails);
    }

    // Resume
    @PostMapping("/resume")
    public WorkFlowState resumePipeline(@RequestParam String workflowid,
            @RequestParam boolean approved) {
        return service.resumeWorkflow(workflowid, approved);
    }

    // Pending
    @GetMapping("/pending")
    public Map<String, WorkFlowState> pendingPipeline() {
        return service.getPendingApprovals();
    }

}
