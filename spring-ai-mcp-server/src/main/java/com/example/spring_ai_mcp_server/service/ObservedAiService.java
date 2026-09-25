package com.example.spring_ai_mcp_server.service;

import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class ObservedAiService {
    private final ChatModel chatModel;
    private final MeterRegistry meterRegistry;
    private final Counter prompttokencounter;
    private final Counter completiontokenCounter;
    private final Counter TotalTokenCounter;
    private final Timer inferenceTimer;

    // constructor
    public ObservedAiService(ChatModel chatModel, MeterRegistry meterRegistry) {
        this.chatModel = chatModel;
        this.meterRegistry = meterRegistry;

        // custom micrometer to track prompttoken counts across calls
        this.prompttokencounter = Counter.builder("genai.tokens.prompt").description("Total user prompt sent to LLM")
                .tags("model", "Qwen/Qwen3.8-27B").register(meterRegistry);

        // custom micrometer to track completion/generationtoken counts for every user
        // prompt-request
        this.completiontokenCounter = Counter.builder("genai.token.completion")
                .description("Total generation token utilized by LLM").tags("model", "Qwen/Qwen3.8-27B")
                .register(meterRegistry);

        // custom micrometer to track TotalToken=Prompt token+Completion Toekn
        this.TotalTokenCounter = Counter.builder("genai.token.totaltoken")
                .description("Total Token Utilized by User and LLM").tags("model", "Qwen/Qwen3.8-27B")
                .register(meterRegistry);

        // Timer to track end-to-end local inference latency
        this.inferenceTimer = Timer.builder("genai.inference.latency").description("Time taken by LLM to get response")
                .tags("model", "Qwen/Qwen3.8-27B").register(meterRegistry);

    }

    public AiMetricResult askModel(String userMessage) {

        long starttime = System.nanoTime();

        // invoke groq model
        ChatResponse chatResponse = chatModel.call(new Prompt(userMessage));

        long durationanos = System.nanoTime() - starttime;
        long durationmillis = TimeUnit.NANOSECONDS.toMillis(durationanos);

        // record latency
        inferenceTimer.record(durationanos, TimeUnit.NANOSECONDS);

        // extract exact token usage
        long prompttokens = 0;
        long completiontokens = 0;
        long totaltokens = 0;

        if (chatResponse.getMetadata() != null && chatResponse.getMetadata() != null) {
            prompttokens = chatResponse.getMetadata().getUsage().getPromptTokens();
            completiontokens = chatResponse.getMetadata().getUsage().getCompletionTokens();
            totaltokens = chatResponse.getMetadata().getUsage().getTotalTokens();

            // increment token meter counter
            prompttokencounter.increment(prompttokens);
            completiontokenCounter.increment(completiontokens);
            TotalTokenCounter.increment(totaltokens);

        }
        String outputtext = chatResponse.getResult().getOutput().getText();

        return new AiMetricResult(
                userMessage,
                outputtext,
                prompttokens,
                completiontokens,
                totaltokens,
                durationanos);
    }

    public record AiMetricResult(String userprompt,
            String response,
            long prompttoken,
            long completiontoken,
            long totaltoken,
            long latency) {
    }

}
