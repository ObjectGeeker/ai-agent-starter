package com.zephyr.ai.domain.agent.service.assembly.mcp.client.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.adk.JsonBaseModel;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.SseServerParameters;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.ToolMcpCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * SSE 传输方式的 MCP 工具集创建服务
 */
@Service
@Slf4j
public class SSEToolMcpCreateService implements ToolMcpCreateService {
    @Override
    public List<BaseToolset> buildMcpToolSet(AiAgentConfigTableVO.Module.Agent.ToolMcp toolMcp) {
        if (null == toolMcp.getSse()) {
            log.debug("agent auto assembly mcp tool empty !");
            return CollUtil.newArrayList();
        }
        SseServerParameters sseServerParameters = SseServerParameters.builder()
                .url(toolMcp.getSse().getBaseUri())
                .sseEndpoint(toolMcp.getSse().getSseEndpoint())
                .timeout(Duration.ofSeconds(toolMcp.getSse().getRequestTimeout()))
                .build();
        McpToolset mcpToolset = new McpToolset(sseServerParameters, JsonBaseModel.getMapper(), List.of(toolMcp.getSse().getName()));
        return CollUtil.newArrayList(mcpToolset);
    }
}
