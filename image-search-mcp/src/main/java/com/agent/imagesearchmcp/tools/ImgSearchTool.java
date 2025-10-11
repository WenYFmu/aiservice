package com.agent.imagesearchmcp.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImgSearchTool {
    private final String API_URL = "https://api.pexels.com/v1/search";
    private final String API_KEY = "iT6aFZLKFcE6VH1q7FNiIFmdlSiwE1lAh05wQJJNeKZPj1Um4BE09rfW";

    @Tool(description = "Search for pictures on an English website")
    public String searchImage(@ToolParam(description = "The English keywords for the picture(only be in English)") String query) {
        List<String> photos = searchMediumImages(query);
        return String.join(",", photos);
    }
    /**
     * 搜索中等尺寸的图片列表
     *
     * @param query
     * @return
     */
    public List<String> searchMediumImages(String query) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("query", query);
        String response = HttpUtil.createGet(API_URL)
                .header("Authorization", API_KEY)
                .form(paramMap)
                .execute()
                .body();
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject) photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
