package com.agent.aiservice.Agent;

import cn.hutool.core.util.StrUtil;
import com.agent.aiservice.Agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {

    //智能体名称
    private String name;
    //运行状态
    private AgentState agentState = AgentState.IDLE;
    //模型
    private ChatClient chatClient;
    //最大执行步数
    private Integer maxStep = 10;
    private Integer currentStep = 0;
    //提示词
    private String systemPrompt;
    private String nextStepPrompt;
    //对话上下文，后面实现自主控制的工具调用所以自己维护 todo:实现对话上下文的id，持久化存储
    private List<Message> messageList = new ArrayList<>();

    public String run(String userPrompt){
        //校验参数
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("userPrompt用户提示词为空");
        }
        //校验状态
        if(this.agentState != AgentState.IDLE){
            throw new RuntimeException("无法运行智能体；状态" + this.agentState);
        }
        agentState = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();

        for(int i = 0; i < maxStep && agentState == AgentState.RUNNING; i++){
            try {
                int stepNum = ++currentStep;
                String stepResult = step();// 智能体结束将调用结束工具改变AgentState.FINISHED
                results.add("执行" + stepNum + stepResult);
                //超过最大执行步数将终止
                if(currentStep >= maxStep){
                    results.add("执行次数达到最大次数" + currentStep + "/" + maxStep);
                    agentState = AgentState.FINISHED;
                }
            } catch (Exception e) {
                agentState = AgentState.ERROR;
                log.error("智能体执行错误", e);
                return "执行错误" + e.getMessage();
            }
        }
        return String.join("\n", results);
    }

    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(300000L);//5分钟超时
        CompletableFuture.runAsync(() -> {
            try {
                //校验参数
                if (StrUtil.isBlank(userPrompt)) {
                    //在异步的情况下异常会被当成结果直到完成被隐藏无法try捕获
                    //throw new RuntimeException("userPrompt用户提示词为空");
                    sseEmitter.complete();
                    sseEmitter.send("错误：提示词输入为空");
                    return;//在主进程已经返回。
                }
                //校验状态
                if (this.agentState != AgentState.IDLE) {
                    //throw new RuntimeException("无法运行智能体；状态" + this.agentState);
                    sseEmitter.complete();
                    sseEmitter.send("错误：智能体状态错误" + this.agentState);
                    return;
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
            agentState = AgentState.RUNNING;
            messageList.add(new UserMessage(userPrompt));
            try {
                for (int i = 0; i < maxStep && agentState == AgentState.RUNNING; i++) {
                        int stepNum = ++currentStep;
                        String stepResult = step();// 智能体结束将调用结束工具改变AgentState.FINISHED
                        sseEmitter.send("执行：" + stepNum + stepResult);
                        //超过最大执行步数将终止
                }
                if (currentStep >= maxStep) {
                    sseEmitter.send("执行次数达到最大次数" + currentStep + "/" + maxStep);
                    sseEmitter.complete();
                    agentState = AgentState.FINISHED;
                }
                sseEmitter.complete();
            }catch (Exception e){
                agentState = AgentState.ERROR;
                log.error("{}智能体执行错误", getName(), e);
                try {
                    sseEmitter.send("智能体执行错误");
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(e);
                }
            }
        });
        sseEmitter.onCompletion(() ->{
            if(agentState == AgentState.RUNNING){
                agentState = AgentState.FINISHED;
            }
        });
        sseEmitter.onTimeout(() ->{
            agentState = AgentState.ERROR;
            log.warn("{}智能体执行超时", getName());
        });
        return sseEmitter;
    }

    public abstract String step();
}
