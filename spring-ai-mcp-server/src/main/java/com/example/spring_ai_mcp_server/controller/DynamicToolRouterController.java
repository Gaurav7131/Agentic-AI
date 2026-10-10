package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.DynamicToolRouterService;

@RestController
@RequestMapping("/api/pruned")
public class DynamicToolRouterController {
    private final DynamicToolRouterService service;

    // constructor
    public DynamicToolRouterController(DynamicToolRouterService service) {
        this.service = service;
    }

    @GetMapping("/execute")
    public Map<String, Object> executeTool(
            @RequestParam(defaultValue = "Please Check my account Balance") String prompt) {
        long startTime = System.currentTimeMillis();

        Map<String, String> response = service.routeAndExecute(prompt);

        return Map.of("prompt", prompt,
                "response", response != null ? response : "No response Generated",
                "excutionLatencyMs.", (System.currentTimeMillis() - startTime));
    }
}
