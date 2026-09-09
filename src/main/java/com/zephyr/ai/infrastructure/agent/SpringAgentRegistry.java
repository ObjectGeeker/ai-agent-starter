package com.zephyr.ai.infrastructure.agent;

import cn.hutool.extra.spring.SpringUtil;
import com.google.adk.plugins.BasePlugin;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.AgentRegistry;
import org.springframework.stereotype.Component;

/**
 * 基于 Spring 容器的 Agent 注册表实现
 * <p>
 * 将 Agent 注册为 Spring Bean（beanName = agentId），
 * 运行时通过容器查找，隔离领域层对 Spring 基础设施的直接依赖。
 */
@Component
public class SpringAgentRegistry implements AgentRegistry {

    @Override
    public void register(String agentId, AiAgentRegisterVO agent) {
        SpringUtil.registerBean(agentId, agent);
    }

    @Override
    public AiAgentRegisterVO lookup(String agentId) {
        return SpringUtil.getBean(agentId, AiAgentRegisterVO.class);
    }

    @Override
    public BasePlugin lookupPlugin(String pluginName) {
        return SpringUtil.getBean(pluginName, BasePlugin.class);
    }

    @Override
    public Object lookupBean(String beanName) {
        return SpringUtil.getBean(beanName);
    }

}
