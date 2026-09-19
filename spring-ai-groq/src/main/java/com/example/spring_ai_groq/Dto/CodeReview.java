package com.example.spring_ai_groq.Dto;

import java.util.List;

//Java Structured Output Record:Schema for Model to strict return validjson by pattern matching using immutable Java Record obj
//Define the target output schema using std java records
public record CodeReview(
        String summary,
        int qualityScore,
        List<String> keyRisks,
        List<String> recommendations) {
}
