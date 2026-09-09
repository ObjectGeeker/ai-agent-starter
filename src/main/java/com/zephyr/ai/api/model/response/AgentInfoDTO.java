package com.zephyr.ai.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 配置信息DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentInfoDTO {

    private String agentName;

    private String agentDesc;

    private String agentId;

}
