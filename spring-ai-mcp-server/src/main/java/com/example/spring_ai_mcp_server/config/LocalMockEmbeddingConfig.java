package com.example.spring_ai_mcp_server.config;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;

@Configuration
public class LocalMockEmbeddingConfig {

    private static final int VECTOR_DIMENSION = 384;

    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        return new AbstractEmbeddingModel() {
            @Override
            public @NonNull EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = request.getInstructions().stream()
                        .map(text -> new Embedding(generateVector(text), 0))
                        .toList();
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public @NonNull float[] embed(Document document) {
                return generateVector(document.getText());
            }

            @Override
            public @NonNull float[] embed(String text) {
                return generateVector(text);
            }

            private float[] generateVector(String text) {
                float[] vector = new float[VECTOR_DIMENSION];
                int hash = (text != null) ? text.hashCode() : 0;
                for (int i = 0; i < VECTOR_DIMENSION; i++) {
                    vector[i] = (float) Math.sin(hash + i);
                }
                return vector;
            }
        };
    }
}