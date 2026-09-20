package com.example.spring_ai_groq.Config;

import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.context.annotation.Bean;

public class AiConfig {
    @Bean
    public InMemoryChatMemoryRepository chatMemoryRepository() {
        // In-memory rep0;swapin inredis repositories
        return new InMemoryChatMemoryRepository();
    }
}
