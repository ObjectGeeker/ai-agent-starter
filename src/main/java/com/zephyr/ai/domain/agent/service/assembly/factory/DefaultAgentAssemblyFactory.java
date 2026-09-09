package com.zephyr.ai.domain.agent.service.assembly.factory;

import com.google.adk.agents.BaseAgent;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.service.assembly.node.AgentAssemblyRootNode;
import lombok.*;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Agent 装配工厂：提供装配策略路由树的根节点入口
 */
@Component
public class DefaultAgentAssemblyFactory {

    private final AgentAssemblyRootNode rootNode;

    public DefaultAgentAssemblyFactory(AgentAssemblyRootNode rootNode) {
        this.rootNode = rootNode;
    }

    public AgentAssemblyRootNode rootNode() {
        return this.rootNode;
    }

    /**
     * Agent 装配上下文
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends cn.bugstack.wrench.design.framework.tree.DynamicContext {

        private OpenAiChatModel openAiChatModel;

        @Builder.Default
        private Map<String, BaseAgent> baseAgentMap = new HashMap<>();

        /**
         * 原子安全的递进步骤
         */
        @Builder.Default
        private AtomicInteger currentStepIndex = new AtomicInteger(0);

        private AiAgentConfigTableVO.Module.MultiAgent currentMultiAgent;

        public List<BaseAgent> queryBaseAgent(List<String> agentNames) {
            return agentNames.stream().map(baseAgentMap::get).collect(Collectors.toList());
        }

    }

}
