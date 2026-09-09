package com.zephyr.ai.domain.agent.model.valobj.properties;

import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "agent.auto.config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAgentConfigProperties {

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enable = true;

    /**
     * Agent 配置表
     */
    private Map<String, AiAgentConfigTableVO> configTableMap;

}
