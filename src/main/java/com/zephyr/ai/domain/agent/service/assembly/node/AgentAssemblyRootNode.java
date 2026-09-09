package com.zephyr.ai.domain.agent.service.assembly.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Agent 装配策略路由树的根节点：不做业务处理，直接路由到 ChatModel 节点
 */
@Component
@Slf4j
public class AgentAssemblyRootNode extends AbstractAgentAssemblySupport {

    private final AgentAssemblyChatModelNode chatModelNode;

    public AgentAssemblyRootNode(AgentAssemblyChatModelNode chatModelNode) {
        this.chatModelNode = chatModelNode;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        log.debug("agent auto assembly root node start");

        // 根节点不做任何处理

        log.debug("agent auto assembly root node end");
        return router(agentAssemblyCommandEntity, dynamicContext);
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        return chatModelNode;
    }
}
