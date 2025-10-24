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
    }

    @Test
    void doChatWithMCP() {
    }

    @Test
    void doChatWithMCPSse() {
    }
}