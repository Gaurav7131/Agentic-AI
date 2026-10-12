package com.example.spring_ai_mcp_server.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import com.example.spring_ai_mcp_server.model.RagEvaluationModel;

//Adaptive rag orchiestator
@Service
public class AdaptiveRagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // constructor
    public AdaptiveRagService(ChatClient.Builder chatBuilder, VectorStore vectorStore) {
        this.chatClient = chatBuilder.build();
        this.vectorStore = vectorStore;
    }

    // A3).AdaptiveRagResponse
    public RagEvaluationModel.AdaptiveRagResponse executeAdaptiveRag(String originalQuery) {
        String currentQuery = originalQuery;
        int maxAttempts = 2;
        int attempts = 0;
        List<Document> matchedDocs = List.of();

        while (attempts < maxAttempts) {
            attempts++;

            // 1.Vector search
            matchedDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(currentQuery).topK(2).similarityThreshold(0.7).build());

            // 2.Document Relevancy Grading(Confidence score)
            if (!matchedDocs.isEmpty() && isContextRelevant(currentQuery, matchedDocs)) {
                break;// If relevant context not found break
            }
            // 3.Self correction:Re-write query
            currentQuery = rewriteQuery(currentQuery);
        }

        // Edge case
        if (matchedDocs.isEmpty()) {
            return new RagEvaluationModel.AdaptiveRagResponse(originalQuery, currentQuery, attempts,
                    "Verified document context could not be found to answer this query safely.", false);
        }

        // 4.Grounded(factual) Generation
        String contextText = matchedDocs.stream().map(Document::getText).reduce("", (a, b) -> a + "\n" + b);

        String finalAnswer = chatClient.prompt()
                .system("Answer STRICTLY using only the provided context. If not present, state you cannot answer.")
                .user(u -> u.text("Context:\n{context}\n\nQuestion: {question}")
                        .param("context", contextText)
                        .param("question", originalQuery))
                .call()
                .content();

        // Return
        return new RagEvaluationModel.AdaptiveRagResponse(currentQuery, originalQuery, attempts, finalAnswer, true);
    }

    // 2-4. isContextRelevant
    private boolean isContextRelevant(String query, List<Document> docs) {
        String docSamples = docs.get(0).getText();
        // A1)DocumentGrade record
        RagEvaluationModel.DocumentGrade grade = chatClient.prompt()
                .system("You are an automated document grader. Determine if the passage is relevant to the user query.")
                .user(u -> (u.text("Passage:{docs}\nQuery:{query}").param("doc", docSamples)).param("query", query))
                .call().entity(RagEvaluationModel.DocumentGrade.class);

        return grade != null && grade.relevance();
    }

    // 3-5.rewriteQuery
    public String rewriteQuery(String query) {
        // A2)ReWrittenQuery record
        RagEvaluationModel.RewrittenQuery rewrote = chatClient.prompt()
                .system("Rewrite the user search query to be more specific and optimized for semantic vector search")
                .user(query).call().entity(RagEvaluationModel.RewrittenQuery.class);

        return rewrote != null ? rewrote.optimizedQuery() : query;
    }
}
