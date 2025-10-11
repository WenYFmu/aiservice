package com.agent.aiservice.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceAppDocumentLoaderTest {

    @Resource
    private ServiceAppDocumentLoader serviceAppDocumentLoader;
    @Test
    void loadJsonAsDocuments() {
        List<Document> documents = serviceAppDocumentLoader.loadJsonAsDocuments();
    }
}