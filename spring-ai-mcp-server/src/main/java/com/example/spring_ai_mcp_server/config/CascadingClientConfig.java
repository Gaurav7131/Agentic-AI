package com.example.spring_ai_mcp_server.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CascadingClientConfig {
    @Bean
    @Qualifier("fastChatClient")
    public ChatClient fastChatClient(ChatClient.Builder fastchatBuilder) {
        // Points to low-cost, ultra-low latency provider (e.g., Groq LPU Llama-3.1-8B)
        return fastchatBuilder.build();
    }

    @Bean
    @Qualifier("frontierChatClient")
    public ChatClient frontierChatClient(ChatClient.Builder frontierchatBuilder) {
        // Points to high-parameter reasoning model (e.g., GPT-4o / Claude 3.5 Sonnet)
        return frontierchatBuilder.build();
    }

}
