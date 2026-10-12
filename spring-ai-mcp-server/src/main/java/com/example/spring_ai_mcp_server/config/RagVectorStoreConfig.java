package com.example.spring_ai_mcp_server.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagVectorStoreConfig {

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Direct instantiation works across milestone versions
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}