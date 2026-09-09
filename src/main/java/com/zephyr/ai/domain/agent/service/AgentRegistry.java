package com.zephyr.ai.domain.agent.service;

import com.google.adk.plugins.BasePlugin;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;

/**
 * Agent 注册表（领域层接口，基础设施层实现）
 * <p>
 * 负责 Agent 装配完成后的注册与运行时查找，
 * 使领域层不直接依赖 Spring 容器等基础设施。
 */
public interface AgentRegistry {

    /**
     * 注册 Agent
     *
     * @param agentId Agent 唯一标识
     * @param agent   Agent 注册信息
     */
    void register(String agentId, AiAgentRegisterVO agent);

    /**
     * 查找已注册的 Agent
     *
     * @param agentId Agent 唯一标识
     * @return Agent 注册信息，未找到时返回 null
     */
    AiAgentRegisterVO lookup(String agentId);

    /**
     * 按名称查找插件
     *
     * @param pluginName 插件 Bean 名称
     * @return 插件实例
     */
    BasePlugin lookupPlugin(String pluginName);

    /**
     * 按名称查找任意 Bean（用于 FunctionTool 等动态工具 Bean 查找）
     *
     * @param beanName Bean 名称
     * @return Bean 实例
     */
    Object lookupBean(String beanName);

}
