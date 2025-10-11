package com.agent.aiservice.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 智能客服文档加载器
 */

@Component
@Slf4j
public class ServiceAppDocumentLoader {
    private final Resource resource;

    public ServiceAppDocumentLoader(@Value("classpath:document/train.json")Resource resource) {
        this.resource = resource;
    }
    //直接将json数组的每一个元素当成一个Document 十分方便
    public List<Document> loadJsonAsDocuments() {
        JsonReader jsonReader = new JsonReader(this.resource, "description", "content");
        return jsonReader.get();
    }

}
