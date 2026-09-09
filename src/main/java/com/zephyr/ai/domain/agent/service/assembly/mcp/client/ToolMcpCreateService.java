package com.zephyr.ai.domain.agent.service.assembly.mcp.client;

import com.google.adk.tools.BaseToolset;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;

import java.util.List;

/**
 * MCP工具创建服务
 */
public interface ToolMcpCreateService {

    /**
     * 创建MCP工具
     *
     * @param toolMcp mcp参数
     * @return Google ADK适配的工具对象
     */
    List<BaseToolset> buildMcpToolSet(AiAgentConfigTableVO.Module.Agent.ToolMcp toolMcp);

}
