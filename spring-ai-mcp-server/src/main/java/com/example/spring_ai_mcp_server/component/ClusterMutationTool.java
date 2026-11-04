package com.example.spring_ai_mcp_server.component;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.spring_ai_mcp_server.service.ClusterTargetResourceService;
import com.example.spring_ai_mcp_server.service.DistributedLeasedManagerService;

//ClusterMutationtool pre-execution lock validation.
@Component
public class ClusterMutationTool {
    private static final Logger log = LoggerFactory.getLogger(ClusterMutationTool.class);
    private final DistributedLeasedManagerService leasedManager;
    private final ClusterTargetResourceService service;

    // constructor
    public ClusterMutationTool(DistributedLeasedManagerService leasedManager, ClusterTargetResourceService service) {
        this.leasedManager = leasedManager;
        this.service = service;
    }

    // Method for decoommionshning pod
    @Tool(description = "Safely Isolates and decommisioned pod ")
    public String decommionPod(@ToolParam(description = "The pod identifier: prod-auth-service-pod-1") String podId,
            @ToolParam(description = "The cloud-deployment-region:ap-south-1") String region) {

        log.info("Attempting lock acquisition for target pod: {} in region: {}", podId, region);

        String agentId = "agent-worker-" + Thread.currentThread().threadId();
        // 1. Acquire lease (TTL = 10s) and receive monotonic fencing token
        Optional<Long> tokenOpt = leasedManager.acquireMutationLock(podId, agentId, 10);

        if (tokenOpt.isEmpty()) {
            log.warn("Conflict detected pod:{} is actively leased by another agent replica", podId);
            return "MUTATION_REJECTED RESOURCE: " + podId + " is actively run by another agent workflow.";
        }

        long fencingToken = tokenOpt.get();
        String idempotencyKey = "idemp-" + podId + "-" + fencingToken;

        try {
            log.info("Executing safe isolation and drain on pod: {} with fencing token: {}", podId, fencingToken);
            Thread.sleep(1000); // simulate drain duration

            // 2. Validate token and mutate target resource
            boolean applied = service.appliedDecommisionWithFencing(
                    podId, region, fencingToken, idempotencyKey);

            if (!applied) {
                return "MUTATION_ABORTED_STALE_TOKEN: Lease expired mid-operation. Target resource rejected token "
                        + fencingToken;
            }

            return "SUCCESS: POD: " + podId + " in region: " + region
                    + " is safely isolated and decommissioned (Token: " + fencingToken + ").";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Failed: Isolation halted due to exception";
        } finally {
            leasedManager.releasedMutationLock(podId);
            log.info("Distributed lease released for pod: {}", podId);
        }
    }
}
