package com.zephyr.ai.domain.agent.service.assembly;

import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Agent 装配策略路由抽象基类：统一多线程策略路由的泛型参数，子类只需实现 doApply 与 get
 */
public abstract class AbstractAgentAssemblySupport extends AbstractMultiThreadStrategyRouter<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> {

    @Override
    protected void multiThread(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        // 不是所有子类都需要实现这个方法，所以由父类实现
    }

}
