package com.agent.aiservice.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 文档转向量数据库（基于内存）
 */
//@Configuration
public class ServiceAppVectorStoreConfig {
    @Resource
    private ServiceAppDocumentLoader serviceAppDocumentLoader;

    @Bean
    VectorStore ServiceAppVectorStore(@Qualifier("ollamaEmbeddingModel")EmbeddingModel embeddingModel) {
        List<Document> documentList = serviceAppDocumentLoader.loadJsonAsDocuments();
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        //todo 将文档存放到数据库当中
        //文档加载到内存当中每次启动服务器都要执行数据的向量化吗？
        vectorStore.doAdd(documentList);
        return vectorStore;
    }
}
