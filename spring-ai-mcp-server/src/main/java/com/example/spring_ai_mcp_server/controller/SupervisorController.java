package com.example.spring_ai_mcp_server.controller;

import org.springframework.web.bind.annotation.*;
import com.example.spring_ai_mcp_server.service.SupervisorAgentService;
import java.util.Map;

@RestController
@RequestMapping("/api/orchestrator")
public class SupervisorController {

    private final SupervisorAgentService supervisorService;

    // constructor
    public SupervisorController(SupervisorAgentService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @GetMapping("/route")
    public Map<String, String> handleTicket(
            @RequestParam(defaultValue = "Alert: pod titan-auth-service is failing in production") String ticket) {

        String result = supervisorService.orchiestrate(ticket);
        return Map.of("ticket", ticket,
                "orchestratedResolution", result != null ? result : "Routing failed");
    }
}