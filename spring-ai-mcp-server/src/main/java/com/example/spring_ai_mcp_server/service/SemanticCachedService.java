package com.example.spring_ai_mcp_server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class SemanticCachedService {
    private final EmbeddingModel embeddingModel;

    private final Map<String, CachedRecord> cache = new ConcurrentHashMap<>();

    // Constant Set Threshold
    private static final double SIMILARITY_THRESHOLD = 0.92;

    // record:CacheRecord
    public record CachedRecord(String query, float[] vector, String response) {
    }

    // constructor
    public SemanticCachedService(EmbeddingModel model) {
        this.embeddingModel = model;
    }

    public String getResponseIfSimiliar(String userPrompt) {
        // Prevent NullPointerException in ConcurrentHashMap
        if (userPrompt == null || userPrompt.isBlank()) {
            return null;

        }
        // vector(meaning) conversion-number
        float[] promptVector = embeddingModel.embed(userPrompt);

        // looping cache
        for (CachedRecord record : cache.values()) {
            double similarity = computeCosineSimilarity(promptVector, record.vector());

            // threshold
            if (similarity > SIMILARITY_THRESHOLD) {
                return "[SEMANTIC CACHE HIT (Score: " + String.format("%.2f", similarity) + ")] " + record.response();

            }
        }
        return null;// if cache miss
    }

    public void put(String userprompt, String response) {
        if (userprompt == null || response == null || response.isBlank()) {
            return;
        }
        float[] vector = embeddingModel.embed(userprompt);
        cache.put(userprompt, new CachedRecord(userprompt, vector, response));
    }

    private double computeCosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];// have same flow(direction)
            normA += Math.pow(vectorA[i], 2);// } have ~ strength(power)
            normB += Math.pow(vectorB[i], 2);

        }
        // (A.B/(||(A)||*||(B)||) & 0.0 has different context
        return (normA == 0) || (normB == 0) ? 0.0 : (dotProduct / Math.sqrt(normA) * Math.sqrt(normB));

    }

}
