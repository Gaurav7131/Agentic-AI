package com.example.spring_ai_mcp_server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.spring_ai_mcp_server.service.ClusterTargetResourceService;
import com.example.spring_ai_mcp_server.service.DistributedLeasedManagerService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FencingTokenMidFlightExpiryTest {

    @Autowired
    private DistributedLeasedManagerService leaseManager;

    @Autowired
    private ClusterTargetResourceService service;

    @Test
    @DisplayName("Verify stale fencing token is rejected at the target resource when lease expires mid-flight")
    void testStaleFencingTokenRejectedAtTarget() throws InterruptedException {
        String podId = "titan-core-1";
        String region = "ap-south-1";

        // 1. Agent 1 acquires lease with 1 second TTL
        Long token1 = leaseManager.acquireMutationLock(podId, "agent-2", 5).orElseThrow();
        assertThat(token1).isNotNull();

        // 2. Simulate Agent 1 stalling mid-flight (1.5s sleep > 1.0s TTL)
        Thread.sleep(1500);

        // 3. Agent 2 acquires lease because Agent 1's TTL expired; gets incremented
        // token
        Long token2 = leaseManager.acquireMutationLock(podId, "agent-2", 5).orElseThrow();
        assertThat(token2).isGreaterThan(token1);

        // 4. Agent 2 completes mutation first at target resource
        boolean agent2Write = service.appliedDecommisionWithFencing(
                podId, region, token2, "idemp-agent-2");
        assertThat(agent2Write).isTrue();

        // 5. Agent 1 resumes and attempts to write with stale token 1
        boolean agent1StaleWrite = service.appliedDecommisionWithFencing(
                podId, region, token1, "idemp-agent-1");

        // Must be rejected at the target resource layer
        assertThat(agent1StaleWrite).isFalse();
    }
}
