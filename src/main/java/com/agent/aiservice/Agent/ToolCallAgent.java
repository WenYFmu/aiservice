package com.agent.aiservice.Agent;

import cn.hutool.core.util.StrUtil;
import com.agent.aiservice.Agent.model.AgentState;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ToolCallAgent extends ReActAgent {

    //可用工具
    private final ToolCallback[] availableTools;

    //自定义调用工具
    private final ChatOptions chatOptions;

    //工具管理 调用工具
    private ToolCallingManager toolCallingManager;

    //MCP工具
    private ToolCallbackProvider toolCallbackProvider;
    //工具调用信息 大模型响应
    private ChatResponse toolCallResponse;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        toolCallingManager = ToolCallingManager.builder().build();
        chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }
    public ToolCallAgent(ToolCallback[] availableTools, ToolCallbackProvider toolCallbackProvider) {
        super();
        this.availableTools = availableTools;
        this.toolCallbackProvider = toolCallbackProvider;
        toolCallingManager = ToolCallingManager.builder().build();
        chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }
    @Override
    protected boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        try {
            //执行思考
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .toolCallbacks(availableTools)
                    .toolCallbacks(toolCallbackProvider)
                    .call()
                    .chatResponse();
            this.toolCallResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            //输出提示信息
            String result = assistantMessage.getText();
            log.info(getName() + "的思考" + result);
            log.info(getName() + "调用了" + toolCalls.size() + "个工具");
            String toolCallInfo = toolCalls.stream()
                    .map(toolCall -> String.format("工具名称 %s, 工具参数 %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if(toolCalls.isEmpty()) {
                //无需调用工具 维护上下文
                getMessageList().add(assistantMessage);
                return false;
            }else {
                //底层自动保存所有上下文 等act执行一并设置
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题：" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    @Override
    protected String act() {
        if(!toolCallResponse.hasToolCalls()){
            return "没有调用工具";
        }
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        //维护上下文
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage)toolExecutionResult.conversationHistory().getLast();
        //检查是否调用任务结束工具
        boolean doTerminate = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate") || response.name().equals("askUser"));
        if(doTerminate){
            //状态修改为完成
            setAgentState(AgentState.FINISHED);
        }
        //获取最后一条信息为工具调用信息
        String result = toolResponseMessage.getResponses().stream()
                .map(ms -> ms.name() + "工具调用结果" + ms.responseData())
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果{}", result);

        return result;
    }
}
