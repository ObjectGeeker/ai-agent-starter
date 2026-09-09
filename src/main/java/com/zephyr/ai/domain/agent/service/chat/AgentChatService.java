package com.zephyr.ai.domain.agent.service.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionResponse;
import com.google.genai.types.Part;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.entity.AgentChatCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AgentResponseDTO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.AgentRegistry;
import com.zephyr.ai.domain.agent.service.IAgentChatService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent 对话服务实现：通过 AgentRegistry 查找已装配的 Runner，执行普通对话与 SSE 流式对话
 */
@Service
@Slf4j
public class AgentChatService implements IAgentChatService {

    private final AgentRegistry agentRegistry;

    public AgentChatService(AgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
    }

    @Override
    public String createSession(String userId, String agentId) {
        // 获取runner
        AiAgentRegisterVO aiAgentRegisterVO = getRegisteredAgent(agentId);
        // 使用runner创建session
        Session session = aiAgentRegisterVO.getRunner().sessionService().createSession(aiAgentRegisterVO.getAppName(), userId).blockingGet();
        // 返回session id
        return session.id();
    }

    @Override
    public String chat(AgentChatCommandEntity agentChatCommandEntity) {
        // 获取runner
        AiAgentRegisterVO aiAgentRegisterVO = getRegisteredAgent(agentChatCommandEntity.getAgentId());
        // 构造消息
        Content content = Content.builder().role("user").parts(Part.fromText(agentChatCommandEntity.getUserMessage())).build();
        // 执行对话
        Flowable<Event> eventFlowable = aiAgentRegisterVO.getRunner().runAsync(agentChatCommandEntity.getUserId(), agentChatCommandEntity.getSessionId(), content, RunConfig.builder().build());
        List<String> outputs = new ArrayList<>();
        eventFlowable.blockingForEach(event -> {
            outputs.add(event.stringifyContent());
        });
        return CollUtil.join(outputs, "\n");
    }

    @Override
    public void streamChat(AgentChatCommandEntity agentChatCommandEntity, SseEmitter sseEmitter) {
        // 获取runner
        AiAgentRegisterVO aiAgentRegisterVO = getRegisteredAgent(agentChatCommandEntity.getAgentId());
        // 构造消息
        Content content = Content.builder().role("user").parts(Part.fromText(agentChatCommandEntity.getUserMessage())).build();
        // 执行对话，异步订阅流式事件并逐条推送；保存订阅句柄，连接断开/超时时取消订阅
        Flowable<Event> eventFlowable = aiAgentRegisterVO.getRunner().runAsync(agentChatCommandEntity.getUserId(), agentChatCommandEntity.getSessionId(), content, RunConfig.builder().streamingMode(RunConfig.StreamingMode.SSE).build());
        Disposable disposable = eventFlowable.subscribe(event -> {
            // 将事件内容转换为规范响应体后推送
            event.content()
                    .flatMap(Content::parts)
                    .orElse(List.of())
                    .forEach(part -> {
                        AgentResponseDTO response = convertPart(part);
                        if (null == response) {
                            return;
                        }
                        try {
                            sseEmitter.send(response, MediaType.APPLICATION_JSON);
                        } catch (IOException e) {
                            log.error("流式推送消息失败", e);
                            sseEmitter.completeWithError(e);
                        }
                    });
        }, error -> {
            log.error("流式对话执行异常", error);
            sseEmitter.completeWithError(error);
        }, sseEmitter::complete);
        sseEmitter.onTimeout(disposable::dispose);
        sseEmitter.onCompletion(disposable::dispose);
    }

    /**
     * 校验并获取已初始化的 Agent 注册信息，未初始化时抛出业务异常
     */
    private AiAgentRegisterVO getRegisteredAgent(String agentId) {
        AiAgentRegisterVO aiAgentRegisterVO = agentRegistry.lookup(agentId);
        if (null == aiAgentRegisterVO) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "此 Agent 未初始化");
        }
        return aiAgentRegisterVO;
    }

    /**
     * 将单个 Part 转换为规范响应体，无有效内容时返回 null
     */
    private AgentResponseDTO convertPart(Part part) {
        // AI 文本
        if (part.text().isPresent()) {
            return AgentResponseDTO.builder()
                    .role("ai")
                    .content(part.text().get())
                    .build();
        }
        // 工具调用
        if (part.functionCall().isPresent()) {
            FunctionCall functionCall = part.functionCall().get();
            AgentResponseDTO.ToolCall toolCall = AgentResponseDTO.ToolCall.builder()
                    .id(functionCall.id().orElse(null))
                    .toolName(functionCall.name().orElse(null))
                    .build();
            return AgentResponseDTO.builder()
                    .role("tool")
                    .toolCalls(List.of(toolCall))
                    .build();
        }
        // 工具调用返回内容
        if (part.functionResponse().isPresent()) {
            FunctionResponse functionResponse = part.functionResponse().get();
            AgentResponseDTO.ToolCall toolCall = AgentResponseDTO.ToolCall.builder()
                    .id(functionResponse.id().orElse(null))
                    .toolName(functionResponse.name().orElse(null))
                    .toolResponse(functionResponse.response().map(JSONUtil::toJsonStr).orElse(null))
                    .build();
            return AgentResponseDTO.builder()
                    .role("tool")
                    .toolCalls(List.of(toolCall))
                    .build();
        }
        return null;
    }
}
