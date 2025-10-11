package com.agent.aiservice.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class AskHumanTool {

    @Tool(description = """
            When you need the user to input something, call this tool.
            This could be to ask the user for assistance or to require the user to provide information, etc.
            Please use it as soon as possible when you need it.
            """)
    public String askUser(@ToolParam(description = "What was said to the user") String said) {
        return "任务结束；最终回答:" + said;
    }
}
