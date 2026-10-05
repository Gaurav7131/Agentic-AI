package com.example.spring_ai_mcp_server.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.ResilientAgentService;

@RestController
@RequestMapping("/api/agent/saga")
public class ResilentAgentWorflowController {
    private final ResilientAgentService service;

    // constructor
    public ResilentAgentWorflowController(ResilientAgentService service) {
        this.service = service;
    }

    // Execute
    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestParam String prompt) {
        return service.processUserIntent(prompt, 1000.0);
    }

    // Resume
    @PostMapping("/resume")
    public Map<String, Object> resume(@RequestParam String checkpointId,
            @RequestParam boolean approved) {
        return service.resumeExecution(checkpointId, approved);
    }

}
