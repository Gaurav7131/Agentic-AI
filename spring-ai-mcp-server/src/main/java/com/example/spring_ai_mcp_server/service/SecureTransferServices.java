package com.example.spring_ai_mcp_server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class SecureTransferServices {
    private static final Logger log = LoggerFactory.getLogger(SecureTransferServices.class);

    private final Map<String, Double> account = new ConcurrentHashMap<>();
    private final Map<String, PendingApproval> pendingApproval = new ConcurrentHashMap<>();

    public SecureTransferServices() {
        account.put("Acc-101", 101.01);
        account.put("Acc-102", 3000.00);
    }

    @Tool(description = "Initiate Transfer Funds from source account to destination account")
    public String requestTranfer(
            @ToolParam(description = "Source Account") String fromAccount,
            @ToolParam(description = "Destination Account") String toAccount,
            @ToolParam(description = "Total Amount") Double amount) {

        log.info("Evaluating funds transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

        if (!account.containsKey(fromAccount) || !account.containsKey(toAccount)) {
            return "Transfer failed: One or both accounts do not exist.";
        }

        // Human-in-the-Loop (HITL): Require human intervention for high-risk transfers
        if (amount > 500.00) {
            String approvalID = "REQ-" + System.currentTimeMillis();
            pendingApproval.put(approvalID, new PendingApproval(fromAccount, toAccount, amount));

            log.warn("Transaction halted. Requires human supervisor approval. Ticket ID: {}", approvalID);
            return String.format("Transaction exceeds $500 threshold! Halted for human approval. Ticket ID: %s",
                    approvalID);
        }

        // Auto-approve transactions (amount <= 500.00)
        executeTransferLogic(fromAccount, toAccount, amount);
        return String.format("Successfully auto-transferred $%.2f from %s to %s.", amount, fromAccount, toAccount);
    }

    @Tool(description = "Approve and execute a halted financial transaction using its ticket ID")
    public String approveAndExecute(
            @ToolParam(description = "The approval ticket ID starting with REQ-") String approvalId) {

        PendingApproval approval = pendingApproval.remove(approvalId);

        if (approval == null) {
            return "Approval Failed: Invalid or expired Ticket ID. Try again after permitting authorization.";
        }

        executeTransferLogic(approval.from(), approval.to(), approval.amount());
        return String.format("Supervisor approved! Transferred $%.2f from %s to %s. Ticket ID: %s",
                approval.amount(), approval.from(), approval.to(), approvalId);
    }

    private void executeTransferLogic(String from, String to, Double amount) {
        account.put(from, account.get(from) - amount); // Deduct from source
        account.put(to, account.get(to) + amount); // Credit to destination
    }

    public record PendingApproval(String from, String to, Double amount) {
    }
}