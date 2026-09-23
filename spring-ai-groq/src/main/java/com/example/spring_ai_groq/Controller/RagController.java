package com.example.spring_ai_groq.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

        private final ChatClient chatClient;
        private final VectorStore vectorStore;

        @Value("classpath:docs/company_policy.txt")
        private Resource policyFile;

        public RagController(ChatClient.Builder chatBuilder, VectorStore vectorStore) {
                this.vectorStore = vectorStore;

                // QuestionAnswerAdvisor automatically intercepts the prompt,
                // performs similarity search on vectorStore, and injects context into the
                // prompt
                QuestionAnswerAdvisor ragAdvisor = new QuestionAnswerAdvisor(
                                vectorStore,
                                SearchRequest.builder().topK(2).similarityThreshold(0.6).build());

                this.chatClient = chatBuilder
                                .defaultSystem("""
                                                    You are a strict company policy assistant.
                                                    Answer the user question ONLY based on the provided context.
                                                    If the answer is not present in the context, reply exactly:
                                                    "I do not have access to that information in the provided documentation."
                                                """)
                                .defaultAdvisors(ragAdvisor)
                                .build();
        }

        // 1. Document Loader & Splitter Endpoint
        @PostMapping("/load")
        public Map<String, Object> loadAndIndexDocument() {
                // Document Loader: reads clean text from file resource
                TextReader reader = new TextReader(policyFile);
                List<Document> rawDocuments = reader.get();

                // Token Splitter: splits large document into smaller chunks with overlap
                TokenTextSplitter splitter = TokenTextSplitter.builder()
                                .withChunkSize(200)
                                .build();
                List<Document> chunks = splitter.apply(rawDocuments);

                // Vector Store: embeds and saves chunks
                vectorStore.add(chunks);

                return Map.of(
                                "status", "Document successfully indexed",
                                "chunksCount", chunks.size());
        }

        // 2. RAG Execution Endpoint
        @GetMapping("/ask")
        public Map<String, String> askQuestion(
                        @RequestParam(defaultValue = "Why database spikes occured during Black friday") String question) {

                String answer = chatClient.prompt()
                                .user(question)
                                .call()
                                .content();

                return Map.of(
                                "question", question,
                                "answer", answer != null ? answer : "No answer generated");
        }
}