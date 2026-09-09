package com.zephyr.ai.domain.agent.service;

import com.zephyr.ai.domain.agent.model.entity.AgentChatCommandEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Agent 对话服务
 */
public interface IAgentChatService {
    /**
     * 创建会话
     */
    String createSession(String userId, String agentId);

    /**
     * 普通对话
     */
    String chat(AgentChatCommandEntity agentChatCommandEntity);

    /**
     * 流式对话
     */
    void streamChat(AgentChatCommandEntity agentChatCommandEntity, SseEmitter sseEmitter);
}
