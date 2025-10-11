package com.agent.aiservice.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {
    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        //放行前操作
        log.info("Ai Request {}", chatClientRequest);
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        //放行后操作
        log.info("Ai Response {}", chatClientResponseFlux);
        return chatClientResponseFlux;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        //放行前操作
        log.info("Ai Request {}", chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        //放行后操作
        log.info("Ai Response {}", chatClientResponse);
        log.info("回答 {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
        return chatClientResponse;
    }
}
