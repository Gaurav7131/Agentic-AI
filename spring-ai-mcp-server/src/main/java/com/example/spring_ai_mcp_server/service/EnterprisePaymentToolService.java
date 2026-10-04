package com.example.spring_ai_mcp_server.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class EnterprisePaymentToolService {
    @Tool(description = "Fetched Account Balance")
    public String getBalance(@ToolParam Double amount) {
        return "Amount:" + amount + "has 2,500";
    }

    // high-risk mutation tool
    @Tool(description = "Transfer funds from ${} to ${}")
    public String executetranfer(
            @ToolParam(description = "Sender Account:") String sender,
            @ToolParam(description = "Destination Account:") String receiver,
            @ToolParam(description = "Total Amount") Double amount) {
        return "Success: Transfered funds from ${}" + sender + "to" + receiver + "Total Amount Balance is:" + amount;
    }

}
