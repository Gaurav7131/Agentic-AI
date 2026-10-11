package com.example.spring_ai_mcp_server.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.spring_ai_mcp_server.model.TaskPlanModel;

@Service
public class CloudInfraService {
    private static final Logger log = LoggerFactory.getLogger(TaskPlanModel.class);
    private final Set<String> activeResources = ConcurrentHashMap.newKeySet();

    public boolean executeStep(TaskPlanModel.ExecutionStep step) {
        log.info("Executing step {} : {} on {}" + step.stepId(), step.actiontype(), step.targetResource());

        // if ops failed at step 3 rollback
        if (step.stepId() == 3) {
            log.error("Step 3 failed:Storage Volume Unavailable");
            return false;
        }

        activeResources.add(step.targetResource());
        return true;
    }

    // Rollback (saga Compensation-Atomic ops)
    public void rollbackStep(TaskPlanModel.ExecutionStep step) {
        log.warn("Compensation:Rolling back step:{} Releasing {}" + step.stepId(), step.targetResource());
        activeResources.remove(step.targetResource());
    }

    public Set<String> getActiveResources() {
        return activeResources;
    }
}
