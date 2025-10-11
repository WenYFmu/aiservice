package com.agent.aiservice.Agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceAgentTest {
    @Resource
    private ServiceAgent serviceAgent;

    @Test
    public void run(){
        String run = serviceAgent.run("帮我看看电脑能不能保修，并帮找一张电脑图片");
        System.out.println(run);
        Assertions.assertNotNull(run);
    }
}