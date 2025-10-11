package com.agent.imagesearchmcp.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ImgSearchToolTest {

    @Resource
    private ImgSearchTool imgSearchTool;

    @Test
    void searchImage() {
        String s = imgSearchTool.searchImage("伊雷娜");
        System.out.println(s);
        assertNotNull(s);
    }
}
