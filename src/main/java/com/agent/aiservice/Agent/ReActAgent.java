package com.agent.aiservice.Agent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ReActAgent extends BaseAgent {


    /**
     * 调用LLM判断是否需要调用工具 根据响应判断 并且维护上下文
     * @return 是否调用工具
     */
    protected abstract boolean think();

    /**
     * 执行工具调用
     * @return 调用结果
     */
    protected abstract String act();

    @Override
    public String step() {
        try {
            if(think()) {
                return act();
            }
            return "思考完成无需调用工具";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "执行步骤失败" + e.getMessage();
        }
    }
}
