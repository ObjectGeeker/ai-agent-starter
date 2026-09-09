package com.zephyr.ai.domain.agent.service.assembly.node.multiagent;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LoopAgent;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import com.zephyr.ai.domain.agent.service.assembly.node.AgentAssemblyMultiAgentNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loop 多智能体装配节点：构造 LoopAgent（子智能体循环执行直至满足退出条件）
 */
@Component
@Slf4j
public class AgentAssemblyLoopAgentNode extends AbstractAgentAssemblySupport {

    private final ObjectProvider<AgentAssemblyMultiAgentNode> multiAgentNodeProvider;

    public AgentAssemblyLoopAgentNode(ObjectProvider<AgentAssemblyMultiAgentNode> multiAgentNodeProvider) {
        this.multiAgentNodeProvider = multiAgentNodeProvider;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        AiAgentConfigTableVO.Module.MultiAgent currentMultiAgent = dynamicContext.getCurrentMultiAgent();
        List<String> subAgents = currentMultiAgent.getSubAgents();
        List<BaseAgent> agentList = dynamicContext.queryBaseAgent(subAgents);

        LoopAgent loopAgent =
                LoopAgent.builder()
                        .name(currentMultiAgent.getName())
                        .description(currentMultiAgent.getDescription())
                        .subAgents(agentList)
                        .maxIterations(currentMultiAgent.getMaxIterations())
                        .build();

        dynamicContext.getBaseAgentMap().put(loopAgent.name(), loopAgent);
        return router(agentAssemblyCommandEntity, dynamicContext);
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        // 路由回中转节点（ObjectProvider 延迟解析，打破循环依赖）
        return multiAgentNodeProvider.getObject();
    }
}
