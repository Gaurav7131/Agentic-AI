package com.example.agent.a2a.orchestrator;

import com.example.agent.a2a.model.A2AContracts.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class A2ADynamicOrchestrator {

    private final RestClient restClient;
        // Discovered registry of remote agent cards
            private final Map<String, AgentCard> discoveredAgents = new ConcurrentHashMap<>();

                public A2ADynamicOrchestrator(RestClient.Builder restClientBuilder) {
                        this.restClient = restClientBuilder.build();
                            }

                                // Dynamic Discovery: Discover and index agent cards at startup or on-demand
                                    public void registerRemoteAgent(String agentCardUrl) {
                                            AgentCard card = restClient.get()
                                                            .uri(agentCardUrl)
                                                                            .retrieve()
                                                                                            .body(AgentCard.class);

                                                                                                    if (card != null) {
                                                                                                                discoveredAgents.put(card.agentId(), card);
                                                                                                                        }
                                                                                                                            }

                                                                                                                                // Horizontal Routing: Dispatches task to the best qualified remote agent
                                                                                                                                    public AgentTaskResponse routeTask(String requiredCapability, String taskIntent, Map<String, Object> params) {
                                                                                                                                            AgentCard matchedAgent = discoveredAgents.values().stream()
                                                                                                                                                            .filter(card -> card.capabilities().contains(requiredCapability))
                                                                                                                                                                            .findFirst()
                                                                                                                                                                                            .orElseThrow(() -> new IllegalStateException("No A2A Agent registered for capability: " + requiredCapability));

                                                                                                                                                                                                    AgentTaskRequest request = new AgentTaskRequest(
                                                                                                                                                                                                                    UUID.randomUUID().toString(),
                                                                                                                                                                                                                                    "session-tenant-100",
                                                                                                                                                                                                                                                    taskIntent,
                                                                                                                                                                                                                                                                    params
                                                                                                                                                                                                                                                                            );

                                                                                                                                                                                                                                                                                    // Execute remote agent-to-agent delegation
                                                                                                                                                                                                                                                                                            return restClient.post()
                                                                                                                                                                                                                                                                                                            .uri(matchedAgent.endpointUrl())
                                                                                                                                                                                                                                                                                                                            .body(request)
                                                                                                                                                                                                                                                                                                                                            .retrieve()
                                                                                                                                                                                                                                                                                                                                                            .body(AgentTaskResponse.class);
                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                }