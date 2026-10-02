package com.example.spring_ai_mcp_server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final Map<String, BigDecimal> account = new ConcurrentHashMap<>();
    private final Map<String, PendingApproval> pendingApproval = new ConcurrentHashMap<>();

    public SecureTransferServices() {
        account.put("Acc-101", BigDecimal.valueOf(101.01));
        account.put("Acc-102", BigDecimal.valueOf(3000.00));
    }

    @Tool(description = "Initiate fund transfer from source account to destination account")
    public String requestTransfer(
            @ToolParam(description = "Source Account ") String fromAccount,
            @ToolParam(description = "Destination Account)") String toAccount,
            @ToolParam(description = "Transfer Amount") Double amount) {

        log.info("Evaluating funds transfer: {} -> {}, amount: {}", fromAccount, toAccount, amount);

        if (amount == null || amount <= 0) {
            return "Transfer failed: Amount must be greater than zero.";
        }

        if (!account.containsKey(fromAccount) || !account.containsKey(toAccount)) {
            return "Transfer failed: One or both accounts do not exist.";
        }

        BigDecimal transferAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal currentBalance = account.get(fromAccount);

        if (currentBalance.compareTo(transferAmount) < 0) {
            return String.format("Transfer failed: Insufficient balance in %s. Available: $%.2f",
                    fromAccount, currentBalance);
        }

        // HITL(human in the Loop)Threshold Check: requires supervisor approval if >$500
        if (transferAmount.compareTo(BigDecimal.valueOf(500.00)) > 0) {
            String approvalId = "REQ-" + System.currentTimeMillis();
            pendingApproval.put(approvalId, new PendingApproval(fromAccount, toAccount, transferAmount));

            log.warn("Transfer exceeds $500. Halted for human approval. Ticket ID: {}", approvalId);
            return String.format("Transaction exceeds $500 threshold! Halted for human approval. Ticket ID: %s",
                    approvalId);
        }

        // auto-approve (amount<500.00)
        executeTransferLogic(fromAccount, toAccount, transferAmount);
        return String.format("Successfully auto-transferred $%.2f from %s to %s.",
                transferAmount, fromAccount, toAccount);
    }

    public String approveAndExecute(String approvalId) {
        PendingApproval approval = pendingApproval.remove(approvalId);

        if (approval == null) {
            return "Approval Failed: Invalid or expired Ticket ID.";
        }

        // Re-check balance at execution time
        BigDecimal currentBalance = account.get(approval.from());
        if (currentBalance.compareTo(approval.amount()) < 0) {
            return String.format("Approval Failed: Source account %s no longer has sufficient balance.",
                    approval.from());
        }

        executeTransferLogic(approval.from(), approval.to(), approval.amount());
        return String.format("Supervisor approved! Transferred $%.2f from %s to %s. Ticket ID: %s",
                approval.amount(), approval.from(), approval.to(), approvalId);
    }

    public Map<String, BigDecimal> getBalances() {
        return Map.copyOf(account);
    }

    private synchronized void executeTransferLogic(String from, String to, BigDecimal amount) {
        account.put(from, account.get(from).subtract(amount));
        account.put(to, account.get(to).add(amount));
    }

    public record PendingApproval(String from, String to, BigDecimal amount) {
    }
}