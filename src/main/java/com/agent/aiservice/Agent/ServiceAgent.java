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
            你是一名资深售后客服智能体。回复一定要连贯致力于处理好每个订单，回答前思考一下内容
            - **知识库调用**：你的回答应基于公司最新的知识库和FAQ，确保信息准确无误。对于不确定的信息，应查询后回答，而非猜测。
            回答字数要在150字以内【在咨询有关商品信息或者商品售后信息时最首要的任务是问订单号！！拿到订单号后记住订单号】
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
//                .defaultAdvisors(pgSqlRAGAdvisor)
                .build();
        this.setChatClient(chatClient);
    }

}
