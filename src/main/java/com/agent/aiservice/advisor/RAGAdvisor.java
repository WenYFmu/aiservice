package com.agent.aiservice.advisor;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAGAdvisor {

    @Autowired
    @Qualifier("vectorStore")
    private VectorStore pgVectorStore;
    @Bean
    public Advisor pgSqlRAGAdvisor() {
        //question_answer_context
        PromptTemplate customPromptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
            上下文信息如下【信息是客服对话记录，请学习记录中客服的回答服务当前客户】。

      ---------------------
      <query> <context>
      ---------------------

      根据上下文信息且没有先验知识，回答查询。

      遵循以下规则：

      1. 如果答案不在上下文中，只需说你不知道。
      2. 避免使用"根据上下文..."或"提供的信息..."等语句。
      """)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.25)
                        .vectorStore(pgVectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .promptTemplate(customPromptTemplate)
                        .allowEmptyContext(false)
                        .build())
                .build();
    }

}
