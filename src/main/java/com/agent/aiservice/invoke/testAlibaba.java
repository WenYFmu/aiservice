package com.agent.aiservice.invoke;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 实现CommandLineRunner接口在项目启动时会运行一次
 */
//@Component
public class testAlibaba implements CommandLineRunner {
    
    @Resource
    private ChatModel dashScopeChatModel;

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    @Override
    public void run(String... args) throws Exception {
        ChatResponse output = dashScopeChatModel.call(new Prompt("你好"));
        System.out.println(output.getResult().getOutput().getText());
    }
}
