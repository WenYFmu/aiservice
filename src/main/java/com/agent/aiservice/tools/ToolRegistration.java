package com.agent.aiservice.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一工具注册
 */
@Configuration
public class ToolRegistration {
    @Bean
    public ToolCallback[] toolCallback(){
        TerminateTool terminateTool = new TerminateTool();
        AskHumanTool askHumanTool = new AskHumanTool();
        AfterSalesFormTool afterSalesFormTool = new AfterSalesFormTool();
        ReturnOrderTool returnOrderTool = new ReturnOrderTool();
        return ToolCallbacks.from(terminateTool, afterSalesFormTool, returnOrderTool);
    }
}
