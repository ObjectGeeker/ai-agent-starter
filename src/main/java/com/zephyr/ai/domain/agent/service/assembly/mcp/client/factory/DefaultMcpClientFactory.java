package com.zephyr.ai.domain.agent.service.assembly.mcp.client.factory;

import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.ToolMcpCreateService;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.impl.SSEToolMcpCreateService;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.impl.StdioToolMcpCreateService;
import org.springframework.stereotype.Component;

/**
 * MCP 客户端工厂：根据 ToolMcp 配置分发到 SSE 或 Stdio 创建服务
 */
@Component
public class DefaultMcpClientFactory {

    private final SSEToolMcpCreateService sseToolMcpCreateService;

    private final StdioToolMcpCreateService stdioToolMcpCreateService;

    public DefaultMcpClientFactory(SSEToolMcpCreateService sseToolMcpCreateService, StdioToolMcpCreateService stdioToolMcpCreateService) {
        this.sseToolMcpCreateService = sseToolMcpCreateService;
        this.stdioToolMcpCreateService = stdioToolMcpCreateService;
    }

    public ToolMcpCreateService getToolMcpCreateService(AiAgentConfigTableVO.Module.Agent.ToolMcp toolMcp) {
        if (toolMcp.getStdio() != null) return stdioToolMcpCreateService;
        if (toolMcp.getSse() != null) return sseToolMcpCreateService;
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "not support mcp create type");
    }
}
