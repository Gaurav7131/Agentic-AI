package com.example.spring_ai_mcp_server.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.spring_ai_mcp_server.component.ScriptPolicyEnforcer;
import com.example.spring_ai_mcp_server.model.SandBoxModels;

@Service
public class AgentSandboxService {
    private static final Logger log = LoggerFactory.getLogger(AgentSandboxService.class);
    private static final long TIMEOUT_MS = 1500;// hard boundary for agent execution-time

    private final ScriptPolicyEnforcer policyEnforcer;
    private final ExecutorService sandboxpool = Executors.newFixedThreadPool(10);// fixed thread pool

    // constructor
    public AgentSandboxService(ScriptPolicyEnforcer policyEnforcer) {
        this.policyEnforcer = policyEnforcer;
    }

    // 1)Record:ExecutionResult
    public SandBoxModels.ExecutionResult executeSecurely(SandBoxModels.ExecutionRequest request) {
        long startTime = System.currentTimeMillis();

        // stage 1:pre-execution static verification
        List<SandBoxModels.PolicyViolation> violations = policyEnforcer.evaluateSafety(request.strictCode());

        if (!violations.isEmpty()) {
            log.warn("Sandbox execution blocked by policy for context;{}" + request.callerContext());
            return new SandBoxModels.ExecutionResult(false, "EXECUTION_REJECTED", violations,
                    System.currentTimeMillis() - startTime);
        }

        // Stage2:isolated execution with time-bound isolation
        Future<Object> task = sandboxpool.submit(() -> runInIsolatedScope(request.strictCode()));

        try {
            String output = (String) task.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            return new SandBoxModels.ExecutionResult(true, output, List.of(), System.currentTimeMillis() - startTime);

        } catch (TimeoutException e) {
            task.cancel(true);
            log.error("Sandbox execution timeout exceeded for context: {}" + request.callerContext());
            return new SandBoxModels.ExecutionResult(false, "TIMEOUT_TERMINATED",
                    List.of(new SandBoxModels.PolicyViolation("EXECUTION_TIMEOUT",
                            "Execution exceeded 1500ms safety boundary")),
                    System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Sanbox execution encountered runtime errors");
            return new SandBoxModels.ExecutionResult(false, "RUNTIME_EXCEPTION" + e.getMessage(), List.of(),
                    System.currentTimeMillis() - startTime);
        }
    }

    private String runInIsolatedScope(String strictCode) {
        // light-wt evaluation scope
        if (strictCode.contains("throw")) {
            throw new IllegalArgumentException("Script threw explicit execution failure");
        }
        return "SUCCESSFUL_COMPUTATION_OUTPUT: Result for [" + strictCode.trim() + "]";
    }

}
