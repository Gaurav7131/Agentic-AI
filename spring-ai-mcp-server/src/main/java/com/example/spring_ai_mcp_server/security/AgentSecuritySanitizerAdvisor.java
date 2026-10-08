package com.example.spring_ai_mcp_server.security;

import java.util.regex.Pattern;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AgentSecuritySanitizerAdvisor implements CallAroundAdvisor {
    // regex against balast prompt overrides embedded in tool data
    private static final Pattern INJECTION_PATTERN = Pattern
            .compile("(?i)(ignore (all )?previous instructions|system prompt|exfiltrate|delete all|admin=true)");

    @Override
    public @NonNull AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {

        // Request: inspect outgpoing prompt context for injection payloads
        String userTxt = request.userText();

        if (userTxt != null && INJECTION_PATTERN.matcher(userTxt).find()) {
            throw new SecurityException("Critical:Prompt injection detected in agent pipeline.");
        }

        // Response
        AdvisedResponse response = chain.nextAroundCall(request);

        // Sanitized response to verify no data exfiltrate or unintended tool leaked
        String rawOutput = response.response().getResult().getOutput().getText();

        if (rawOutput != null && INJECTION_PATTERN.matcher(rawOutput).find()) {
            throw new SecurityException("CRITICAL: Downstream LLM attempted to execute poisoned directives.");
        }

        return response;

    }

    @Override
    public @NonNull String getName() {
        return "AgentSecuritySanitizerAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;// high priority interceptor
    }
}