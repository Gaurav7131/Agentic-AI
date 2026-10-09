package com.example.spring_ai_mcp_server.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

//This runnable implementation provides a Distributed Tool Leaser & Consensus Service that acquires a temporary lock before any agentic mutation tool executes.
@Service
public class DistributedLeasedManager {
    // Distrubuted lock store
    private final Map<String, Instant> lockRegistry = new ConcurrentHashMap<>();

    // 1)Acquire Lock
    public synchronized boolean acquireMutationLock(String resourceKey, long ttlSec) {
        Instant now = Instant.now();
        Instant expiry = lockRegistry.get(resourceKey);

        if (expiry != null && expiry.isAfter(now)) {
            return false;// resource locked by another agent instance
        }
        // put fresh entry inside lock store
        lockRegistry.put(resourceKey, now.plusSeconds(ttlSec));
        return true;
    }

    // 2)Released Lock
    public synchronized void releasedMutationLock(String resourceKey) {
        lockRegistry.remove(resourceKey);// released entry from lockstore
    }

}
