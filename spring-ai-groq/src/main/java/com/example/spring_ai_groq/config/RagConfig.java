package com.example.spring_ai_groq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Value("classpath:docs/company-policy.txt")
    private Resource privateDocument;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        // 1. Initialize In-Memory SimpleVectorStore with the Embedding Model
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // 2. Load the private document via Tika
        log.info("Loading document: {}", privateDocument.getFilename());
        TikaDocumentReader reader = new TikaDocumentReader(privateDocument);
        List<Document> rawDocuments = reader.read();

        // 3. Split into token chunks (default chunk size ~800 tokens with overlap)
        TokenTextSplitter tokenSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = tokenSplitter.apply(rawDocuments);

        // 4. Ingest and calculate vector embeddings
        log.info("Ingesting {} chunks into SimpleVectorStore...", splitDocuments.size());
        vectorStore.add(splitDocuments);
        log.info("Document indexing completed successfully.");

        return vectorStore;
    }
}