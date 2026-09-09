package com.zephyr.ai.domain.agent.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent 对话规范响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponseDTO {

    /**
     * tool or ai
     */
    private String role;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 工具调用
     */
    private List<ToolCall> toolCalls;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ToolCall {
        /**
         * 工具 id
         */
        private String id;
        /**
         * 工具名称
         */
        private String toolName;
        /**
         * 工具调用的返回内容
         */
        private String toolResponse;
    }

}
