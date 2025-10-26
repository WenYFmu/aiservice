package com.agent.aiservice;

import com.agent.aiservice.ServiceAgent.ServiceApp;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootTest
class AiServiceApplicationTests {
    private ChatClient dashScopeChatClient;
    @Test
    void contextLoads() {

    }


    @Autowired
    public ServiceApp serviceApp;
    @Test
    void doChat() {
        UUID uuid = UUID.randomUUID();
        //多轮对话第一轮
        serviceApp.doChat("你好，我是杉木", uuid.toString());
        //多轮对话第二轮
        serviceApp.doChat("商品支持退货吗？？", uuid.toString());
        //多轮对话第三轮
        serviceApp.doChat("我要退货", uuid.toString());
        //多轮对话第四轮
        serviceApp.doChat("我叫什么名字?", uuid.toString());
    }

    @Test
    void doChatWithRag() {
        UUID uuid = UUID.randomUUID();
        StringBuilder say = new StringBuilder();
        //多轮对话第一轮
        say.append(serviceApp.doChatWithRAG("你好，我是杉木", uuid.toString()));
        //多轮对话第二轮WithRAG
        say.append(serviceApp.doChatWithRAG("商品支持退货吗？？", uuid.toString()));
        //多轮对话第三轮WithRAG
        say.append(serviceApp.doChatWithRAG("订单号是123456", uuid.toString()));
        //多轮对话第四轮WithRAG
        say.append(serviceApp.doChatWithRAG("我要退货", uuid.toString()));
        //多轮对话第五轮WithRAG
        say.append(serviceApp.doChatWithRAG("我刚刚和你说了订单号和需求你全忘了？没忘的话复述一遍", uuid.toString()));
        System.out.println(say);
    }

    @Test
    void doChatWithMCP() {
        UUID uuid = UUID.randomUUID();
        //多轮对话第一轮
        String s = serviceApp.doChatWithMCP("我想要几张笔记本电脑的图片", uuid.toString());
        Assertions.assertNotNull(s);
        System.out.println(s);
    }
}
