package com.zephyr.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring AI OpenAiChatModel 连通性测试
 */
@SpringBootTest(classes = Application.class)
@Slf4j
public class ApiTest {

    @Value("${BASEURL}")
    private String baseUrl;
    @Value("${APIKEY}")
    private String apiKey;
    @Value("${MODEL_NAME}")
    private String model;

    @Test
    public void test() {
        // 1. 构建配置选项
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .maxTokens(1024)
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .options(options)
                .build();

        String response = chatModel.call("你好");

        log.info("返回结果: {}", response);

    }

}
