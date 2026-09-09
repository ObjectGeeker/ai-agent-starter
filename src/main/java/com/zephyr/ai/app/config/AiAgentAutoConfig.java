package com.zephyr.ai.app.config;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.properties.AiAgentConfigProperties;
import com.zephyr.ai.domain.agent.service.IAgentAssemblyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Agent 自动装配配置：应用就绪后读取配置表并触发 Agent 装配
 */
@Configuration
@EnableConfigurationProperties({AiAgentConfigProperties.class})
@Slf4j
public class AiAgentAutoConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final AiAgentConfigProperties properties;

    private final ObjectMapper objectMapper;

    private final IAgentAssemblyService agentAssemblyService;

    public AiAgentAutoConfig(AiAgentConfigProperties properties, ObjectMapper objectMapper, IAgentAssemblyService agentAssemblyService) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.agentAssemblyService = agentAssemblyService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (null == properties || CollUtil.isEmpty(properties.getConfigTableMap())) {
            log.info("agent config properties empty");
            return;
        }
        if (!properties.isEnable()) {
            log.info("agent auto config not enabled");
            return;
        }
        Map<String, AiAgentConfigTableVO> configTableMap = properties.getConfigTableMap();
        try {
            log.info("agent config table {}", objectMapper.writeValueAsString(configTableMap));
            agentAssemblyService.acceptAssemblyAgents(CollUtil.newArrayList(configTableMap.values()));
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "JSON序列化出错");
        }
    }
}
