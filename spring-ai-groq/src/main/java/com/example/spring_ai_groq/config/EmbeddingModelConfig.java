package com.example.spring_ai_groq.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EmbeddingModelConfig {

    private static final int VECTOR_DIMENSION = 384;

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel fallbackEmbeddingModel() {
        return new AbstractEmbeddingModel() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = request.getInstructions().stream()
                        .map(text -> new Embedding(generateVector(text), 0))
                        .toList();
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                return generateVector(document.getFormattedContent());
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