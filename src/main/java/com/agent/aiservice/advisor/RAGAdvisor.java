package com.agent.aiservice.advisor;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
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
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.25)
                        .vectorStore(pgVectorStore)
                        .build())
                .build();
    }
}
