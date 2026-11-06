package com.example.spring_ai_mcp_server.service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

//This runnable implementation provides a Distributed Tool Leaser & Consensus Service that acquires a temporary lock before any agentic mutation tool executes.
@Service
public class DistributedLeasedManagerService {
    // Record:LeaseRecord
    public record LeaseRecord(String holderId, long fencingToken, Instant expiresAt) {
    }

    // Distrubuted lock store
    private final Map<String, LeaseRecord> leaseRegistry = new ConcurrentHashMap<>();
    private final AtomicLong tokenGen = new AtomicLong(0);

    public synchronized Optional<Long> acquireMutationLock(String resourceId, String agentId, long ttlSec) {
        Instant now = Instant.now();
        LeaseRecord currentLease = leaseRegistry.get(resourceId);

        // if actively held by another agent replicas& not expires;rejected
        if (currentLease != null && currentLease.expiresAt().isAfter(now) && !currentLease.holderId().equals(agentId)) {
            return Optional.empty();// resource locked by another agent instance
        }

        // Monotonically token increment
        long nextFencingToken = tokenGen.incrementAndGet();
        leaseRegistry.put(resourceId, new LeaseRecord(agentId, nextFencingToken, now));

        return Optional.of(nextFencingToken);
    }

    // 2)Released Lock
    public synchronized void releasedMutationLock(String resourceKey) {
        leaseRegistry.remove(resourceKey);// released entry from lockstore
    }

}
