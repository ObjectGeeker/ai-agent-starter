package com.zephyr.ai.domain.agent.service.assembly;

import cn.hutool.core.collection.CollUtil;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.service.IAgentAssemblyService;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import com.zephyr.ai.domain.agent.service.assembly.node.AgentAssemblyRootNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent 装配服务实现：遍历配置表，通过策略路由树完成 Agent 装配
 */
@Service
@Slf4j
public class AgentAssemblyService implements IAgentAssemblyService {

    private final DefaultAgentAssemblyFactory defaultAgentAssemblyFactory;

    public AgentAssemblyService(DefaultAgentAssemblyFactory defaultAgentAssemblyFactory) {
        this.defaultAgentAssemblyFactory = defaultAgentAssemblyFactory;
    }

    @Override
    public void acceptAssemblyAgents(List<AiAgentConfigTableVO> tables) {
        if (CollUtil.isEmpty(tables)) {
            log.info("agent config table empty");
            return;
        }
        AgentAssemblyRootNode agentAssemblyRootNode = defaultAgentAssemblyFactory.rootNode();
        if (null == agentAssemblyRootNode) {
            log.error("agent auto assembly root node not initialized !");
            return;
        }
        for (AiAgentConfigTableVO table : tables) {
            try {
                agentAssemblyRootNode.apply(AgentAssemblyCommandEntity.builder().configTable(table).build(), new DefaultAgentAssemblyFactory.DynamicContext());
            } catch (Exception e) {
                log.error("agent auto assembly error", e);
            }
        }
        log.info("agent auto assembly end");
    }
}
