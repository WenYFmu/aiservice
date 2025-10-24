package com.agent.aiservice.prompt;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;

import java.util.Map;

public class MyPromptTemplate {

    public String constructPrompts(){
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
            告诉我 5 部由 <composer> 作曲的电影名称。
            """)
                .build();

       return promptTemplate.render(Map.of("composer", "John Williams"));
    }
}
