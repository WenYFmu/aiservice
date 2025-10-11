package com.agent.aiservice.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class TerminateTool {

    @Tool(description = """
                          Terminate the interaction when the request is met OR   
                          if the assistant cannot proceed further with the task.  
                          When you have finished all the tasks, call this tool to end the work.
                          """)
    public String doTerminate(@ToolParam(description = "Respond to the user based on the information provided by the calling tool.") String answer) {
        return answer;
    }
}
