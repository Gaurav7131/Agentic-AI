package com.example.spring_ai_mcp_server.SubAgent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

//Subagent Context Isolation & deterministic execution:Every subagent has isolated tool traces(@tool set),has seperate chatClient to prevent it fromcontext window Pollution and tool selection degradation.
//SubAgent A:ItTriagentAgent
@Service
public class ItTriageSubagent {
    private final ChatClient chatClient;

    // constructor
    public ItTriageSubagent(ChatClient.Builder chatbuilder) {
        this.chatClient = chatbuilder.defaultSystem("""
                You are a specialized DevOps Triage Agent. Analyze infra issues and call triage tools.
                """).defaultTools(this).build();
    }

    // tool
    @Tool(description = "Checks the health status of a target Kubernetes pod.")
    public String checkPodHealth(String podName) {
        return "POD_Status" + podName + "Is Crashed due to performance degradation";
    }

    public String executeQuery(String query) {
        return chatClient.prompt().user(query).call().content();
    }

}
