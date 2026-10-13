package com.example.spring_ai_mcp_server.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.spring_ai_mcp_server.model.RoutingContract.ExecutionResults;
import com.example.spring_ai_mcp_server.service.CascadingAgentOrchestrator;

@RestController
@RequestMapping("/api/cascade-agent")
public class CascadingAgentController {
    private final CascadingAgentOrchestrator service;

    // constructor
    public CascadingAgentController(CascadingAgentOrchestrator service) {
        this.service = service;

    }

    @GetMapping("/execute")
    public Map<String, Object> executeModel(
            @RequestParam(defaultValue = "How the Claude switch from cheap model to high performance model during production without halting execution comparing with other models") String prompt) {

        ExecutionResults results = service.routeAndExecute(prompt);

        return Map.of(
                "prompt", prompt,
                "executionTier", results.executedTier(),
                "toolExecuted", results.executedTool(),
                "output", results.output(),
                "latencyMs", results.latencyMs());
    }

}
