package com.example.spring_ai_groq.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_ai_groq.Dto.CodeReview;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final ChatClient chatClient;

    // constuctor
    public AiController(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.defaultSystem("Hey ur are an agentic ai architect").build();

    }

    // Structured Output: Forces model response into a typed Java
    // Record(CodeReview.java(Schema)
    @PostMapping("/analyze-code")
    public CodeReview analyzeCode(@RequestBody String snippet) {
        return chatClient.prompt()
                .user("Analyze the following code and return an evaluation:\n" + snippet)
                .call()
                .entity(CodeReview.class); // Spring AI generates JSON Schema and deserializes response
    }

    // Token Streaming: Streams generated tokens in real time via Server-Sent Events
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(
            @RequestParam(defaultValue = "Write a comprehensive essay on distributed systems architecture.") String prompt) {

        return chatClient.prompt()
                .user(prompt)
                .stream()// stream-chaining
                .content(); // Emits each token as a reactive Flux<String>
    }
}