package com.agent.aiservice.prompt;

import cn.hutool.core.lang.UUID;
import com.agent.aiservice.advisor.MyLoggerAdvisor;
import com.agent.aiservice.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MyPromptTemplateTest {

    private ChatClient chatClient;

    @Resource
    private ChatModel dashscopeChatModel;

    String fileDir = System.getProperty("user.dir") +"/tmp/chat-memory";//获取用户的应用目录

    @Test
    void constructPrompts() {
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir + UUID.randomUUID());
        MyPromptTemplate myPromptTemplate = new MyPromptTemplate();
        System.out.println(myPromptTemplate.constructPrompts());
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem("你是爱莲，你是售后客服，你专注于回答用户的问题")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(fileBasedChatMemory).build(), new MyLoggerAdvisor())
                .build();
        UserMessage message = new UserMessage("我的订单是100151");
        Prompt prompt = new Prompt(new AssistantMessage("用户咨询的订单号是1544412"));
        System.out.println(chatClient.prompt(prompt).user("我是杉木，我要咨询我的订单号是多少").call().content());
        ChatResponse chatResponse = chatClient.prompt(prompt).user("我的名字是什么，我的订单号是多少").call().chatResponse();
        System.out.println(chatResponse.getResult().getOutput().getText());

    }
}