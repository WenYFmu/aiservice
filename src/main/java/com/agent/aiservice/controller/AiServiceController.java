package com.agent.aiservice.controller;


import cn.hutool.core.util.StrUtil;
import com.agent.aiservice.Agent.ServiceAgent;
import com.agent.aiservice.ServiceAgent.ServiceApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiServiceController {

    @Resource
    private ServiceApp serviceApp;

//    @Resource
//    private ServiceAgent serviceAgent;

    @Resource
    private ToolCallback[] toolCallback;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Resource
    private Advisor pgSqlRAGAdvisor;
    /**
    * 智能客服同步调用
    */
    @GetMapping("/server_app/chat/sync")
    public String syncChat(@RequestParam(value = "query")String query, @RequestParam(value = "chatId")String chatId) {
        return serviceApp.doChat(query, chatId);
    }

    @GetMapping("/server_app/chat/sse")
    public SseEmitter sseChat(@RequestParam(value = "query")String query, @RequestParam(value = "chatId")String chatId) {
        SseEmitter sseEmitter = new SseEmitter(300000L);//超时时间为5min
        serviceApp.doChatWithMCPSse(query, chatId).subscribe(
            chunk -> {
                try {
                    sseEmitter.send(chunk);
                } catch (IOException e) {
                    sseEmitter.completeWithError(e);
                    throw new RuntimeException(e);
                }
            }, sseEmitter::completeWithError, sseEmitter::complete);
    // 使用完sseEmitter后一定要sseEmitter::complete关闭不然会一直连接直到超时
        return sseEmitter;
    }


    @GetMapping("/server_app/agent_chat/sse")
    public SseEmitter agentChat(@RequestParam(value = "query")String query){
        ServiceAgent serviceAgent = new ServiceAgent(toolCallback, dashscopeChatModel, toolCallbackProvider, pgSqlRAGAdvisor);
        if(StrUtil.isBlank(query)){
           throw new RuntimeException("query is null");
        }
        return serviceAgent.runStream(query);
    }
}

