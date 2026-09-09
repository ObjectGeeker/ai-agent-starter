package com.zephyr.ai.domain.agent.model.valobj;

import com.google.adk.runner.Runner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 装配完成实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAgentRegisterVO {

    private String agentId;

    private String appName;

    private String agentName;

    private String agentDesc;

    private Runner runner;

}
