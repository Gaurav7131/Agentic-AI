package com.example.spring_ai_mcp_server.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_mcp_server.service.ObservedAiService;

@RestController
@RequestMapping("/api/observe")
public class AiObservalibiltyController {
    private final ObservedAiService observedAiService;

    public ObservedAiService(ObservedAiService observedAiService){
        this.observedAiService=observedAiService;
    }

    @GetMapping("/chat")
    public ObservedAiService.AiMetricResult chat(
            @RequestParam(defaultValue = "Explain the average latency time of groq for generating response") String response) {
        return ObservedAiService.AiMetricResult();
    }

}
