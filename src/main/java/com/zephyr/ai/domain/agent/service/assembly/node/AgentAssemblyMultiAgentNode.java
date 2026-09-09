package com.zephyr.ai.domain.agent.service.assembly.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.hutool.core.collection.CollUtil;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AgentTypeEnum;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import com.zephyr.ai.domain.agent.service.assembly.node.multiagent.AgentAssemblyLoopAgentNode;
import com.zephyr.ai.domain.agent.service.assembly.node.multiagent.AgentAssemblyParallelAgentNode;
import com.zephyr.ai.domain.agent.service.assembly.node.multiagent.AgentAssemblySequentialAgentNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 多智能体装配中转节点：按步骤遍历 multiAgents 配置，根据类型路由到 loop/parallel/sequential 子节点
 */
@Component
@Slf4j
public class AgentAssemblyMultiAgentNode extends AbstractAgentAssemblySupport {

    private final AgentAssemblyLoopAgentNode loopAgentNode;

    private final AgentAssemblyParallelAgentNode parallelAgentNode;

    private final AgentAssemblySequentialAgentNode sequentialAgentNode;

    private final AgentAssemblyRunnerNode assemblyRunnerNode;

    public AgentAssemblyMultiAgentNode(AgentAssemblyLoopAgentNode loopAgentNode, AgentAssemblyParallelAgentNode parallelAgentNode, AgentAssemblySequentialAgentNode sequentialAgentNode, AgentAssemblyRunnerNode assemblyRunnerNode) {
        this.loopAgentNode = loopAgentNode;
        this.parallelAgentNode = parallelAgentNode;
        this.sequentialAgentNode = sequentialAgentNode;
        this.assemblyRunnerNode = assemblyRunnerNode;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        // 获取多智能体列表
        List<AiAgentConfigTableVO.Module.MultiAgent> multiAgents = agentAssemblyCommandEntity.getConfigTable().getModule().getMultiAgents();
        if (CollUtil.isEmpty(multiAgents)) {
            dynamicContext.setCurrentMultiAgent(null);
            return router(agentAssemblyCommandEntity, dynamicContext);
        }
        // 判断当前的进度
        dynamicContext.setCurrentMultiAgent(multiAgents.get(dynamicContext.getCurrentStepIndex().get()));
        // 增加一步
        dynamicContext.getCurrentStepIndex().incrementAndGet();
        // 直接路由
        return router(agentAssemblyCommandEntity, dynamicContext);
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        AiAgentConfigTableVO.Module.MultiAgent currentMultiAgent = dynamicContext.getCurrentMultiAgent();

        if (null == currentMultiAgent) {
            return assemblyRunnerNode;
        }

        AgentTypeEnum agentType;
        try {
            agentType = AgentTypeEnum.valueOf(currentMultiAgent.getType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "multi agent type not support !");
        }

        return switch (agentType) {
            case loop -> loopAgentNode;
            case parallel -> parallelAgentNode;
            case sequential -> sequentialAgentNode;
        };
    }
}
