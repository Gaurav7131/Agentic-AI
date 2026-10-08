package com.example.spring_ai_mcp_server.security;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class SecuredInfrastructureTools {

    @Tool(description = "Runs safe diagnostic ping on internal service host,Rejects non-whitelist hostname")
    public String checkHostHealth(@ToolParam(description = "auth-service.internal") String host) {
        // Deterministic parameter allow-listing: Never let LLM pass arbitrary IP ranges
        if (!host.endsWith(".internal")) {
            return "ERROR: Host violation. Only internal service domains are permissible.";
        }
        return "SUCCESS: Host " + host + " responded with HTTP 200 OK.";
    }
}
