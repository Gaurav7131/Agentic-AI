package com.example.spring_ai_mcp_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.model.SandBoxModels;
import com.example.spring_ai_mcp_server.service.AgentSandboxService;

@RestController
@RequestMapping("/api/agent/sandbox")
public class AgentSandboxController {
    private final AgentSandboxService sandboxService;

    public AgentSandboxController(AgentSandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @PostMapping("/run")
    public ResponseEntity<SandBoxModels.ExecutionResult> execute(@RequestBody SandBoxModels.ExecutionRequest request) {
        SandBoxModels.ExecutionResult result = sandboxService.executeSecurely(request);
        return ResponseEntity.ok(result);
    }
}
