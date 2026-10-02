package com.example.spring_ai_mcp_server.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.example.spring_ai_mcp_server.SubAgent.AccountSubagent;
import com.example.spring_ai_mcp_server.SubAgent.ItTriageSubagent;

//Supervisor:exposes subagents as specialized tools to route queries autonomously.
@Service
public class SupervisorAgentService {
    private final ChatClient supervisorchatClient;
    private final ItTriageSubagent itAgent;
    private final AccountSubagent accountAgent;

    // constructor
    public SupervisorAgentService(ChatClient.Builder chatBuilder, ItTriageSubagent itAgent,
            AccountSubagent accountAgent) {
        this.supervisorchatClient = chatBuilder.defaultSystem("""
                You are the Master Orchestration Supervisor.
                    Route the incoming enterprise request to the appropriate domain expert:
                    - IT/Infrastructure issues -> delegateToItAgent
                    - Billing/Finance inquiries -> delegateToBillingAgent
                    Synthesize the specialist's findings into a concise, professional executive summary.
                """).defaultTools(this).build();
        this.itAgent = itAgent;
        this.accountAgent = accountAgent;
    }

    // --------------------TaskList & Allocator--------------------------------

    // tool:ItTriageAgent
    @Tool(description = "Delegates infrastructure, cluster, or container triage tasks to the IT Agent.")
    public String delegateToItAgent(String task) {
        return itAgent.executeQuery(task);
    }

    // tool:AccountSubAgent
    @Tool(description = "Delegates enterprise invoice, credit limit, or billing queries to the Billing Agent.")
    public String delegateToBillingAgent(String task) {
        return accountAgent.executeQuery(task);
    }

    // Orchistator:exposes subagents as specialized tools to route queries
    // autonomosly.
    public String orchiestrate(String clientPrompt) {
        return supervisorchatClient.prompt().user(clientPrompt).call().content();

    }

}
