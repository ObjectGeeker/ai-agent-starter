package com.zephyr.ai.domain.agent.service;

import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;

import java.util.List;

/**
 * Agent 装配服务
 */
public interface IAgentAssemblyService {

    /**
     * 执行 Agent 装配
     *
     * @param tables Agent 装配配置表
     */
    void acceptAssemblyAgents(List<AiAgentConfigTableVO> tables);

}
