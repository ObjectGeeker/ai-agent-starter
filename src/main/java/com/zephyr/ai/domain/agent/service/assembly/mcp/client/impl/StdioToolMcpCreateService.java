package com.zephyr.ai.domain.agent.service.assembly.mcp.client.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.adk.JsonBaseModel;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.StdioServerParameters;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.ToolMcpCreateService;
import io.modelcontextprotocol.client.transport.ServerParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Stdio 传输方式的 MCP 工具集创建服务
 */
@Service
@Slf4j
public class StdioToolMcpCreateService implements ToolMcpCreateService {
    @Override
    public List<BaseToolset> buildMcpToolSet(AiAgentConfigTableVO.Module.Agent.ToolMcp toolMcp) {
        if (null == toolMcp.getStdio()) {
            log.debug("agent auto assembly mcp tool empty !");
            return CollUtil.newArrayList();
        }
        AiAgentConfigTableVO.Module.Agent.ToolMcp.StdioServerParameters stdioConfig = toolMcp.getStdio();

        StdioServerParameters stdioServerParameters = StdioServerParameters.builder()
                .command(stdioConfig.getServerParameters().getCommand())
                .args(stdioConfig.getServerParameters().getArgs())
                .build();

        ServerParameters serverParameters = stdioServerParameters.toServerParameters();
        McpToolset mcpToolset = new McpToolset(serverParameters, JsonBaseModel.getMapper(), CollUtil.newArrayList(stdioConfig.getName()));
        return CollUtil.newArrayList(mcpToolset);
    }
}
