package com.example.spring_ai_mcp_server.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

//DistrubutedLeasedManager that ensured the single agent exeuction owenrship.
@Service
public class DistributedLeasedManagerService {
    private final Map<String, Instant> leasedRegistry = new ConcurrentHashMap<>();

    public synchronized boolean acquiredLock(String resourceId, long ttlSeconds) {
        Instant now = Instant.now();
        Instant currentRegistry = leasedRegistry.get(resourceId);

        if (currentRegistry != null && currentRegistry.isAfter(now)) {
            return false;// lock held by another agent replica
        }

        leasedRegistry.put(resourceId, now.plusSeconds(ttlSeconds));
        return true;
    }

    // after suceesful or fail exeuction free up resource
    public synchronized void releasedLock(String resourceId) {
        leasedRegistry.remove(resourceId);
    }

}
