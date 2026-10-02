package com.example.spring_ai_mcp_server.SubAgent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

//SubAgent B:AccountSubAgent
@Service
public class AccountSubagent {
    private final ChatClient chatClient;

    // constructor
    public AccountSubagent(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.defaultSystem("""
                You are a specialized Enterprise Billing Agent. Handle balance checks and credit limits
                """).defaultTools(this).build();
    }

    // tool
    @Tool(description = "Fetches the current ledger balance and subscription tier for an enterprise account.")
    public String getBillingDetails(Long org_Id, String tier) {
        return "ORG_ID:" + org_Id + "has Rs. 4,20,000 credit remaining" + tier + "Tier: Enterprise Gold.";
    }

    public String executeQuery(String query) {
        return chatClient.prompt().user(query).call().content();
    }

}
