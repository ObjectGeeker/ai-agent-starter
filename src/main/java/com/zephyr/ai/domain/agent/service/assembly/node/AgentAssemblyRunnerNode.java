package com.zephyr.ai.domain.agent.service.assembly.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.hutool.core.collection.CollUtil;
import com.google.adk.agents.BaseAgent;
import com.google.adk.plugins.BasePlugin;
import com.google.adk.runner.InMemoryRunner;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.AgentRegistry;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Runner 装配节点：组装 InMemoryRunner 并将 Agent 注册到 AgentRegistry
 */
@Component
@Slf4j
public class AgentAssemblyRunnerNode extends AbstractAgentAssemblySupport {

    private final AgentRegistry agentRegistry;

    public AgentAssemblyRunnerNode(AgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        AiAgentConfigTableVO.Agent agentConfig = agentAssemblyCommandEntity.getConfigTable().getAgent();
        AiAgentConfigTableVO.Module.Runner runnerConfig = agentAssemblyCommandEntity.getConfigTable().getModule().getRunner();

        String agentName = runnerConfig.getAgentName();
        BaseAgent runnerAgent = dynamicContext.getBaseAgentMap().get(agentName);
        List<BasePlugin> plugins = new ArrayList<>();
        if (CollUtil.isNotEmpty(runnerConfig.getPluginNames())) {
            for (String pluginName : runnerConfig.getPluginNames()) {
                plugins.add(agentRegistry.lookupPlugin(pluginName));
            }
        }

        InMemoryRunner runner = new InMemoryRunner(runnerAgent, agentAssemblyCommandEntity.getConfigTable().getAppName(), plugins);

        // 构建注册对象
        AiAgentRegisterVO aiAgentRegisterVO = AiAgentRegisterVO.builder()
                .agentId(agentConfig.getAgentId())
                .appName(agentAssemblyCommandEntity.getConfigTable().getAppName())
                .agentName(agentConfig.getAgentName())
                .agentDesc(agentConfig.getAgentDesc())
                .runner(runner)
                .build();

        // 注册到 AgentRegistry（由基础设施层实现，隔离 Spring 容器依赖）
        agentRegistry.register(agentConfig.getAgentId(), aiAgentRegisterVO);

        return aiAgentRegisterVO;
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
