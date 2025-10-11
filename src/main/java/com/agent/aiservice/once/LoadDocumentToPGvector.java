package com.agent.aiservice.once;

import com.agent.aiservice.rag.ServiceAppDocumentLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LoadDocumentToPGvector {

    @Autowired
    private VectorStore vectorStore;//pgvector

    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Resource
    ServiceAppDocumentLoader serviceAppDocumentLoader;

    public void load() {
        //todo 修改文档转向量的逻辑，sql实现已经有向量的情况下不进行文档转换
        List<Document> documentlist = serviceAppDocumentLoader.loadJsonAsDocuments();
        vectorStore.add(documentlist);
        log.info("Success Loaded（成功加载到pgvector） {} documents", documentlist.size());
    }
}
