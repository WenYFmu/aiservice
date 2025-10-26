package com.agent.aiservice.ServiceAgent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ServiceAppTest {

    @Resource
    private ServiceApp serviceApp;
    @Test
    void doChat() {
        UUID uuid = UUID.randomUUID();
        serviceApp.doChat("你好", uuid.toString());
    }

    @Test
    void doChatWithRAG() {
        String uuid = UUID.randomUUID().toString();
        System.out.println("1" + serviceApp.doChatWithRAG("电脑为什么开不了机", uuid));
        System.out.println("2" + serviceApp.doChatWithRAG("我要退货", uuid));
//        System.out.println("3" + serviceApp.doChatWithRAG("我刚刚不是给你订单号了？？？", uuid));
    }

    @Test
    void doChatWithMCP() {
    }

    @Test
    void doChatWithMCPSse() {
    }
}