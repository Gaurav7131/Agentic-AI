package com.example.spring_ai_mcp_server.service;

import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ResilientClusterTools {

    private final DistributedLeasedManagerService manager;
    private String resourceId;

    // constructor
    public ResilientClusterTools(DistributedLeasedManagerService manager) {
        this.manager = manager;
    }

    // Mutation tool :Tool Sandboxing
    @Tool(description = "Decommissions a target pod. Requires distributed mutation lock.")
    public String decommissionPod(@ToolParam(description = "Name of pod to Terminate") String podName,
            @ToolParam(description = "Cluster partioin Region") String region) {

        String resourceKey = "LOCK:POD" + podName;
        Optional<Long> locked = manager.acquireMutationLock(resourceId, resourceKey, 30);

        if (locked != null) {
            return "MUTATION_REJECTED" + podName + "is leased by another workflow";
        }

        try {
            return "SUCCESS POD:" + podName + "with region:" + region;
        } finally {
            manager.releasedMutationLock(resourceKey);
        }

    }

}
