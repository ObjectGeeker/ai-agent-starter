package com.zephyr.ai.api.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 对话请求体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentChatRequest {

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "AgentID不能为空")
    private String agentId;

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @NotBlank(message = "不能发送空消息")
    private String userMessage;

}
