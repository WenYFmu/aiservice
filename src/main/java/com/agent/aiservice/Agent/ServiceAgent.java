package com.agent.aiservice.Agent;

import com.agent.aiservice.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ServiceAgent extends ToolCallAgent{

    public ServiceAgent(ToolCallback[] availableTools, ChatModel dashscopeChatModel, SyncMcpToolCallbackProvider toolCallbackProvider, Advisor pgSqlRAGAdvisor) {
        super(availableTools, toolCallbackProvider);
        this.setName("客服Agent");
        this.setMaxStep(20);
        String SYSTEM_PROMPT = """
            你是一名资深电商客服智能体【使用doTerminate工具与用户交互】【请不要添加动作描述的词语，正常对话，用最少的字数完成回答】。回答前思考一下内容。
            **重要记忆规则**：
            - 必须记住用户提供的所有重要信息，包括：姓名、订单号、商品信息、联系方式等
            - 当用户提到订单号时，要立即记录并在后续对话中主动使用
            - 如果用户询问之前提到的信息，要能够准确复述
            - 在回答问题时，要结合对话历史中的上下文信息
            除非客户明确表示无需跟进，否则你应主动告知后续联系的方式和时间，做到事事有回音。
            """;

        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)//todo 模板，最终调用时再做修改
                .defaultAdvisors(new MyLoggerAdvisor())
                .defaultAdvisors(pgSqlRAGAdvisor)
                .build();
        this.setChatClient(chatClient);
    }

}
