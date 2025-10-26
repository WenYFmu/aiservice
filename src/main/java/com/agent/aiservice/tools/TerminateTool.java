package com.agent.aiservice.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class TerminateTool {

    @Tool(description = """
                          当满足请求或助手无法继续执行任务时终止交互。完成所有任务后，调用此工具以结束工作。
                          只有该工具能给用户响应信息
                          """)
    public String doTerminate(@ToolParam(description = "Respond to the user based on the information provided by the calling tool.") String answer) {
        return answer;
    }
}
