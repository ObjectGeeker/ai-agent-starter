package com.zephyr.ai.domain.agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 对话命令实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentChatCommandEntity {

    private String userId;

    private String agentId;

    private String sessionId;

    private String userMessage;

}
