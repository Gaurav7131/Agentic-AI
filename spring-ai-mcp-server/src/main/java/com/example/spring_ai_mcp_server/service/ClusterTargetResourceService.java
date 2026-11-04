package com.example.spring_ai_mcp_server.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//The target db or cluster state that checks the fencing token directly before writing.
@Service
public class ClusterTargetResourceService {
    private static final Logger log = LoggerFactory.getLogger(ClusterTargetResourceService.class);

    // Tracks hihest fencing token applied to pod
    private final Map<String, Long> appliedFencingTokens = new ConcurrentHashMap<>();

    // Tracks processed idempotency keys
    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    // Target resource mutation method for idempotency and monotonicity
    public synchronized boolean appliedDecommisionWithFencing(
            String podId,
            String region,
            long incomingFencingTokens,
            String idempotencyKey) {

        // 1) Idempotency check: if already processed under this key, return safely
        if (processedKeys.contains(idempotencyKey)) {
            log.warn("Request already applied:{}", idempotencyKey);
            return true;
        }

        // 2) Fencing token check at resource layer
        long lastAppliedToken = appliedFencingTokens.getOrDefault(podId, 0L);// Long

        // edge condn
        if (incomingFencingTokens <= lastAppliedToken) {
            log.error(
                    "Mutation rejected at Resource:Stale fencing token [{}]. Resource [{}] has already applied token [{}]",
                    incomingFencingTokens, podId, lastAppliedToken);
            return false;
        }

        // 3)Apply state mutation
        appliedFencingTokens.put(podId, incomingFencingTokens);
        processedKeys.add(idempotencyKey);
        log.info("Mutation succeeded at target resource for [{}] with fencing token [{}]", podId,
                incomingFencingTokens);
        return true;

    }
}
