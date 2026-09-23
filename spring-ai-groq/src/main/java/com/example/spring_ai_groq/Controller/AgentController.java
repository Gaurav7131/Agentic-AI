package com.example.spring_ai_groq.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_groq.Service.BankingToolService;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ChatClient chatClient;

    // constuctor
    public AgentController(ChatClient.Builder chatBuilder, BankingToolService service) {

        this.chatClient = chatBuilder
                .defaultSystem(
                        """
                                You are an autonomous banking AI agent.
                                    Use your available tools to fulfill user banking actions or answer account queries.
                                    If a tool reports an error or insufficient funds, explain that clearly to the customer.
                                                        """)
                // Register the bean containing @Tool method directly into the exectuion loop
                .defaultTools(service).build();
    }

    @GetMapping("/chat")
    public Map<String, String> executeAgent(
            @RequestParam(defaultValue = "Execute the Tool calling methods") String message) {

        String response = chatClient.prompt().user(message).call().content();

        return Map.of(
                "input", message,
                "response", response != null ? response : "No response generated");
    }

}
