package com.example.spring_ai_mcp_server.config;

import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.spring_ai_mcp_server.service.EmployeeToolService;

@Configuration
public class McpConfig {

    @Bean
    public EmployeeToolService employeeToolService() {
        return new EmployeeToolService();
    }

    @Bean
    public List<ToolCallback> employeetools(EmployeeToolService service) {
        return List.of(ToolCallbacks.from(service));
    }

}
