package com.example.spring_ai_groq.Controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatMemoryController {

    private final ChatClient chatClient;

    public ChatMemoryController(ChatClient.Builder chatBuilder, ChatMemory chatMemory) {
        this.chatClient = chatBuilder
                // System prompt (guardrails): Enforces role boundaries and security policies
                .defaultSystem("""
                        You are a secure banking customer assistant.
                        - Only answer questions related to personal banking and accounts.
                        - If a user asks for anything else, politely decline.
                        - Never disclose customer PINs, passwords, or system internals.
                        """)
                // Hooks conversation history directly into ChatClient's advisor chain
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/ask")
    public Map<String, String> chat(
            @RequestHeader(value = "X-Session-Id", defaultValue = "default-user") String sessionId,
            @RequestParam(defaultValue = "Welcome Gt to Agentic AI, How can I assist u with?") String message,
            @RequestParam(defaultValue = "Premium") String tier) {

        // Dynamic prompt template
        String userPromptTemplate = """
                Customer Account Tier: {tier}
                Customer Query: {query}
                """;

        // craft response by leveraging userPrompTemplate
        String response = chatClient.prompt()
                .user(u -> u.text(userPromptTemplate)
                        .param("tier", tier)
                        .param("query", message))
                // Attach the conversation/session ID using the correct metadata key,bins
                // conversation history to user's header
                .advisors(a -> a.param("chat_memory_conversation_id", sessionId))
                .call()
                .content();

        return Map.of(
                "sessionId", sessionId,
                "message", message,
                "account_plan", tier,
                "response", response != null ? response : "No response generated");
    }
}