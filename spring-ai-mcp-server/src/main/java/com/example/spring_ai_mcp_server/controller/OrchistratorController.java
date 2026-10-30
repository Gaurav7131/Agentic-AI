package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.model.A2AContracts.AgentTaskResponse;
import com.example.spring_ai_mcp_server.service.A2ADynamicOrchistrator;

//Rest controller for A2A Delegation
@RestController
@RequestMapping("/api/orchestrator")
public class OrchistratorController {
    private final A2ADynamicOrchistrator orchestrator;

    public OrchistratorController(A2ADynamicOrchistrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/discover")
    public String discoverAgent(@RequestParam String cardUrl) {
        orchestrator.registerRemoteAgent(cardUrl);
        return "Agent successfully registered via A2A discovery!";
    }

    @PostMapping("/audit-infra")
    public AgentTaskResponse auditInfrastructure(@RequestParam String manifest) {
        return orchestrator.routeTask(
                "SOC2_COMPLIANCE",
                "Execute security scan on cloud manifest",
                Map.of("spec", manifest));
    }
}
