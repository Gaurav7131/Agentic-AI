package com.example.spring_ai_groq.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class BankingToolService {
    public static final Logger log = LoggerFactory.getLogger(BankingToolService.class);

    // in-memory mock db
    private final Map<String, Double> account = new ConcurrentHashMap<>();

    // constructor
    public BankingToolService() {

        account.put("Account-101", 1000.0);
        account.put("Account-102", 2001.2);
    }

    @Tool(description = "Retrieve the current balance from the account")
    public String getBalance(
            @ToolParam(description = "Unique identifier for the respective account") Long accountId) {

        log.info("Tool calling:getBalance() method invoked" + accountId);

        Double balance = account.get(accountId);
        if (balance == null) {
            return "Account not Found";
        }
        return "Account" + accountId + "has a active balance of Rs." + balance;
    }

    @Tool(description = "Transfer money from one bank account to another target account")
    public String tranferFunds(
            @ToolParam(description = "Source account no") String fromAccount,
            @ToolParam(description = "Destination Account Number") String toAccount,
            @ToolParam(description = "Total Amount in account") double amount) {

        log.info("Tool Called:Transfer funds from {} to {}" + tranferFunds(fromAccount, toAccount, amount));

        if (!account.containsKey(fromAccount) || account.containsKey(toAccount)) {
            return "Transfer failed:AccountId invalid ";

        }
        if (account.get(fromAccount) < amount) {
            return "Transfer failed:Insufficient funds:";
        }

        account.put(fromAccount, account.get(fromAccount) - amount);// deposit
        account.put(toAccount, account.get(toAccount) + amount);// credit
        return "Successfully Transfer funds from " + fromAccount + "to" + toAccount + "with Total Balance :" + amount;
    }
}
