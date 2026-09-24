package com.example.spring_ai_mcp_server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class EmployeeToolService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeToolService.class);

    private Map<String, String> employeeDept = new ConcurrentHashMap<>();

    // constructor
    public EmployeeToolService() {
        employeeDept.put("EMP-101", "JAVA DEVELOPERS");
        employeeDept.put("EMP-102", "PYTHON DEVELOPERS");
        employeeDept.put("EMP-103", "PROMPT ENGINEERING");
    }

    @Tool(description = "Lookup the assigned employee for the role")
    public String getEmployeeDept(
            @ToolParam(description = "The Employee_Id of the employee") String empId) {

        log.info("MCP Execution:Fecthing employee Details" + empId);

        String dept = employeeDept.get(empId);

        if (dept == null) {
            return "Employee:" + empId + "Not Found in records";

        }

        return "Employee:" + empId + "Belongs to the: " + dept;

    }

}
