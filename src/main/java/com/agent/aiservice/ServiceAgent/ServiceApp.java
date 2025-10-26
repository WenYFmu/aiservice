package com.agent.aiservice.ServiceAgent;

import com.agent.aiservice.advisor.MyLoggerAdvisor;
import com.agent.aiservice.chatmemory.FileBasedChatMemory;
import com.agent.aiservice.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class ServiceApp {
    private static final String SYSTEM_PROMPT = """
            你是一名资深电商客服【请不要添加动作描述的词语，正常对话，用最少的字数完成回答】。只要用户是用中文你一定也是中文回复。回复一定要连贯致力于处理好每个订单，回答前思考一下内容。
            
            **重要记忆规则**：
            - 必须记住用户提供的所有重要信息，包括：姓名、订单号、商品信息、联系方式等
            - 当用户提到订单号时，要立即记录并在后续对话中主动使用
            - 如果用户询问之前提到的信息，要能够准确复述
            - 在回答问题时，要结合对话历史中的上下文信息
            
            除非客户明确表示无需跟进，否则你应主动告知后续联系的方式和时间，做到事事有回音。
            请运用你的专业能力，为客户提供卓越的服务体验。
            """;

    private final ChatClient chatClient;

//    @Autowired
//    @Qualifier("ServiceAppVectorStore")
//    private VectorStore serviceAppVectorStore;

    //改用配置类创建RAG
//    @Autowired
//    @Qualifier("vectorStore")
//    private VectorStore pgVectorStore;

    @Resource
    private Advisor pgSqlRAGAdvisor;

    @Resource
    QueryRewriter queryRewriter;//提示词重写

    public ServiceApp(ChatModel dashscopeChatModel) {
        //对话记忆存储对象 对话记忆存储部分
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                //最大记忆条数
                .maxMessages(20)
                .build();

        //对话记忆存储到文件
        String fileDir = System.getProperty("user.dir") +"/tmp/chat-memory";//获取用户的应用目录
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir);

        chatClient = ChatClient.builder(dashscopeChatModel)
                //系统提示词
                //todo 结构化输出 生成订单处理结果 订单号，时间，评价
                .defaultSystem(SYSTEM_PROMPT)
                //注册顾问
                .defaultAdvisors(
                        //补充对话记忆的算法部分(另一种PromptChatMemoryAdvisor)
                        MessageChatMemoryAdvisor.builder(fileBasedChatMemory).build()
                        //自定义日志信息
//                        new MyLoggerAdvisor()
//                      SimpleLoggerAdvisor() // 官方日志实现日志级别是DEBUG
                )
                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                //动态修改advisor参数
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                ;})
                .call()
                .chatResponse();
        if (chatResponse != null) {
            String content = chatResponse.getResult().getOutput().getText();
            log.info(content);
            return content;
        }
        return null;
    }

    //rag对话
    public String doChatWithRAG(String message, String chatId) {
        //todo 用户提示词重写【重写超级笨看看和Context-aware Queries (上下文感知查询)结合效果怎么样】
//        String rewrittenMessage = queryRewriter.doRewriteQuery(message);
        //rag顾问

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                    ;})
                .advisors(new MyLoggerAdvisor())
                .advisors(pgSqlRAGAdvisor)
                .call()
                .chatResponse();
        if (chatResponse != null) {
            return chatResponse.getResult().getOutput().getText();
        }
        return null;
    }

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;
    public String doChatWithMCP(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                    ;})
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        if (chatResponse != null) {
            return chatResponse.getResult().getOutput().getText();
        }
        return null;
    }

    /**
     * SseEmitter 实现sse传输，大概原理是先建立连接后利用响应式的方法send向前端发信息，服务端向客户端传输数据。
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatWithMCPSse(String message, String chatId){
        return chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                    ;
                })
                .advisors(new MyLoggerAdvisor())
                .advisors(pgSqlRAGAdvisor)
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }
}
