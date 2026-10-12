package com.example.spring_ai_mcp_server.controller;

import com.example.spring_ai_mcp_server.service.AdaptiveRagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent/crag")
public class AdaptiveRagController {

    private final AdaptiveRagService ragService;

    public AdaptiveRagController(AdaptiveRagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public Object ask(
            @RequestParam(defaultValue = "What is the policy?") String query) {
        return ragService.executeAdaptiveRag(query);
    }
}