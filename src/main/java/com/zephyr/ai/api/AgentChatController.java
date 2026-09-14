package com.zephyr.ai.api;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.object.common.brick.common.BaseResponse;
import com.object.common.brick.utils.ResultUtil;
import com.zephyr.ai.api.model.request.AgentChatRequest;
import com.zephyr.ai.api.model.response.AgentInfoDTO;
import com.zephyr.ai.domain.agent.model.entity.AgentChatCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.properties.AiAgentConfigProperties;
import com.zephyr.ai.domain.agent.service.IAgentChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent 服务接口
 */
@RestController
@RequestMapping("agent")
@Tag(name = "Agent服务接口")
@SaCheckLogin
public class AgentChatController {

    private final AiAgentConfigProperties properties;

    private final IAgentChatService agentChatService;

    public AgentChatController(AiAgentConfigProperties properties, IAgentChatService agentChatService) {
        this.properties = properties;
        this.agentChatService = agentChatService;
    }

    @GetMapping("query_agent_list")
    @Operation(summary = "查询Agent列表")
    public BaseResponse<List<AgentInfoDTO>> queryAgentList() {
        ArrayList<AiAgentConfigTableVO> agentConfigTables = CollUtil.newArrayList(properties.getConfigTableMap().values());
        List<AgentInfoDTO> agentInfoDTOS = agentConfigTables.stream()
                .map(table -> AgentInfoDTO.builder().agentId(table.getAgent().getAgentId()).agentDesc(table.getAgent().getAgentDesc()).agentName(table.getAgent().getAgentName()).build())
                .collect(Collectors.toList());
        return ResultUtil.success(agentInfoDTOS);
    }

    @GetMapping("create_session")
    @Operation(summary = "创建会话")
    public BaseResponse<String> createSession(@RequestParam String agentId) {
        String sessionId = agentChatService.createSession(currentUserId(), agentId);
        return ResultUtil.success(sessionId);
    }

    @PostMapping("chat")
    @Operation(summary = "非流式对话")
    public BaseResponse<String> chat(@Valid @RequestBody AgentChatRequest agentChatRequest) {
        String response = agentChatService.chat(convertToCommand(agentChatRequest));
        return ResultUtil.success(response);
    }

    @PostMapping("stream_chat")
    @Operation(summary = "流式对话")
    public SseEmitter streamChat(@Valid @RequestBody AgentChatRequest agentChatRequest) {
        SseEmitter sseEmitter = new SseEmitter(1000000L);
        agentChatService.streamChat(convertToCommand(agentChatRequest), sseEmitter);
        return sseEmitter;
    }

    /**
     * 接口层请求体转领域层对话命令
     */
    private AgentChatCommandEntity convertToCommand(AgentChatRequest agentChatRequest) {
        return AgentChatCommandEntity.builder()
                .userId(currentUserId())
                .agentId(agentChatRequest.getAgentId())
                .sessionId(agentChatRequest.getSessionId())
                .userMessage(agentChatRequest.getUserMessage())
                .build();
    }

    /**
     * 获取当前登录用户 ID。调用方已由 {@link SaCheckLogin} 保证处于登录态。
     */
    private String currentUserId() {
        return StpUtil.getLoginIdAsString();
    }

}
