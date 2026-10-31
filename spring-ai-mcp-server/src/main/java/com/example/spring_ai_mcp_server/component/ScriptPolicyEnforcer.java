package com.example.spring_ai_mcp_server.component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import com.example.spring_ai_mcp_server.model.SandBoxModels;
import com.example.spring_ai_mcp_server.model.SandBoxModels.PolicyViolation;

@Component
public class ScriptPolicyEnforcer {

    // Deterministic denied list of high risk confidential operations
    private static final List<Pattern> FORBIDDEN_PATTERNS = List.of(
            Pattern.compile("System\\.(exit|gc)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Runtime\\\\.getRuntime\\(\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ProcessBuilder", Pattern.CASE_INSENSITIVE),
            Pattern.compile("java\\.io\\.File", Pattern.CASE_INSENSITIVE),
            Pattern.compile("java\\.net\\.", Pattern.CASE_INSENSITIVE));

    public List<SandBoxModels.PolicyViolation> evaluateSafety(String code) {
        List<SandBoxModels.PolicyViolation> violations = new ArrayList<>();

        // Edge cond
        if (code == null || code.isBlank()) {
            violations.add(new PolicyViolation("EMPTY PAYLOAD", "Script payload must be mandate"));
            return violations;
        }

        for (Pattern pattern : FORBIDDEN_PATTERNS) {
            if (pattern.matcher(code).find()) {
                violations.add(new SandBoxModels.PolicyViolation("RESTRICTED_API_CALLS",
                        "Execution Halted,Script contains malcious code content"));
            }
        }
        return violations;
    }
}
