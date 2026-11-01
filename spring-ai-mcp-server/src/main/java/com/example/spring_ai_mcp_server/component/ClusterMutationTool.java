package com.example.spring_ai_mcp_server.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.spring_ai_mcp_server.security.DistributedLeasedManager;

//ClusterMutationtool pre-execution lock validation.
@Component
public class ClusterMutationTool {
    private static final Logger log = LoggerFactory.getLogger(ClusterMutationTool.class);
    private final DistributedLeasedManager leasedManager;

    // constructor
    public ClusterMutationTool(DistributedLeasedManager leasedManager) {
        this.leasedManager = leasedManager;
    }

    // Method for decoommionshning pod
    @Tool(description = "Safely Isolates and decommisioned pod ")
    public String decommionPod(@ToolParam(description = "The pod identifier: prod-auth-service-pod-1") String podId,
            @ToolParam(description = "The cloud-deployment-region:ap-south-1") String region) {

        log.info("Attempting lock acquisition for target pod: {} in region: {}" + podId, region);

        // Acquired 30 seconds lock to prevent concurrent replicas execution
        boolean acquired = leasedManager.acquireMutationLock(podId, 10);

        if (!acquired) {
            log.warn("Conflict detected pod:{} is actively leased by another pod:{}" + podId);
            return "MUTATION_REJECTED RESOURCE:" + podId + "is actively run by another workflow";
        }

        // Safe isolation;try-catch
        try {
            log.info("Executing safe isolation and drain on pod: {}" + podId);

            Thread.sleep(1000);// simulate 1 mls time
            return "SUCCESS:POD:" + podId + "in region:" + region + "is safely isolated and decommisiioned";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Failed:Isolation halted due to exception";
        } finally {
            leasedManager.releasedMutationLock(podId);
            log.info("Distributed lease released for pod: {}", podId);

        }
    }

}
