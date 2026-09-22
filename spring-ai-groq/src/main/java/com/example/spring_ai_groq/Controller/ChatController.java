package com.example.spring_ai_groq.controller;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder
                .defaultSystem("You are a helpful assistant.")
                .build();
    }

    @GetMapping("/generate")
    public Map<String, String> generate(
            @RequestParam(defaultValue = "Explain why Groq LPU is so fast") String prompt) {
        try {
            String completion = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return Map.of(
                    "status", "success",
                    "prompt", prompt,
                    "completion", completion);
        } catch (Exception e) {
            return Map.of(
                    "status", "error",
                    "error_type", e.getClass().getSimpleName(),
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }
}