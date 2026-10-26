package com.example.agent.a2a.service;

import com.example.agent.a2a.model.A2AContracts.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/a2a/compliance-agent")
public class ComplianceWorkerAgentController {

    private final ChatClient chatClient;

        public ComplianceWorkerAgentController(ChatClient.Builder builder) {
                this.chatClient = builder
                                .defaultSystem("""
                                                    You are an autonomous Cloud Security & Compliance Agent.
                                                                        Audit the provided infrastructure spec against PCI-DSS and SOC2 standards.
                                                                                            Return an executive assessment with risk rating.
                                                                                                            """)
                                                                                                                            .build();
                                                                                                                                }

                                                                                                                                    // 1. A2A Capability Endpoint (Well-Known Agent Card)
                                                                                                                                        @GetMapping("/.well-known/agent.json")
                                                                                                                                            public AgentCard getAgentCard() {
                                                                                                                                                    return new AgentCard(
                                                                                                                                                                    "agent-sec-compliance-01",
                                                                                                                                                                                    "Enterprise Security & Compliance Auditor",
                                                                                                                                                                                                    "Performs automated compliance audits on cloud deployment manifests.",
                                                                                                                                                                                                                    "http://localhost:8081/api/a2a/compliance-agent/execute",
                                                                                                                                                                                                                                    List.of("INFRA_AUDIT", "PCI_DSS", "SOC2_COMPLIANCE"),
                                                                                                                                                                                                                                                    "BEARER_TOKEN",
                                                                                                                                                                                                                                                                    Map.of("costPerInvocationTokens", 1200, "slaMs", 1500)
                                                                                                                                                                                                                                                                            );
                                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                                                    // 2. A2A Task Execution Endpoint
                                                                                                                                                                                                                                                                                        @PostMapping("/execute")
                                                                                                                                                                                                                                                                                            public AgentTaskResponse executeTask(@RequestBody AgentTaskRequest request) {
                                                                                                                                                                                                                                                                                                    long startTime = System.currentTimeMillis();

                                                                                                                                                                                                                                                                                                            String auditResult = chatClient.prompt()
                                                                                                                                                                                                                                                                                                                            .user("Audit the following spec: " + request.parameters().getOrDefault("spec", ""))
                                                                                                                                                                                                                                                                                                                                            .call()
                                                                                                                                                                                                                                                                                                                                                            .content();

                                                                                                                                                                                                                                                                                                                                                                    return new AgentTaskResponse(
                                                                                                                                                                                                                                                                                                                                                                                    request.taskId(),
                                                                                                                                                                                                                                                                                                                                                                                                    "COMPLETED",
                                                                                                                                                                                                                                                                                                                                                                                                                    "agent-sec-compliance-01",
                                                                                                                                                                                                                                                                                                                                                                                                                                    auditResult,
                                                                                                                                                                                                                                                                                                                                                                                                                                                    System.currentTimeMillis() - startTime
                                                                                                                                                                                                                                                                                                                                                                                                                                                            );
                                                                                                                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                }