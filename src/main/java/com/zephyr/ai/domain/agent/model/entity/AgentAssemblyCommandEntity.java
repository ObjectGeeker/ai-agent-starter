package com.zephyr.ai.domain.agent.model.entity;

import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 装配命令实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentAssemblyCommandEntity {

    private AiAgentConfigTableVO configTable;

}
