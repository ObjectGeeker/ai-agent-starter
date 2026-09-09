package com.zephyr.ai.domain.agent.service.assembly.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ChatModel 装配节点：根据配置构造 Spring AI OpenAiChatModel 并存入上下文
 */
@Component
@Slf4j
public class AgentAssemblyChatModelNode extends AbstractAgentAssemblySupport {

    private final AgentAssemblyBaseAgentsNode agentAssemblyBaseAgentsNode;

    public AgentAssemblyChatModelNode(AgentAssemblyBaseAgentsNode agentAssemblyBaseAgentsNode) {
        this.agentAssemblyBaseAgentsNode = agentAssemblyBaseAgentsNode;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        log.debug("agent auto assembly chat model node start");
        //1. 校验参数
        AiAgentConfigTableVO.Module.ChatModel chatModel = agentAssemblyCommandEntity.getConfigTable().getModule().getChatModel();
        if (null == chatModel) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未定义ChatModel配置！");
        }
        //2. 构造ChatModel（关闭思考模式：多轮工具调用场景下，思考内容经 ADK 适配层转换后会丢失，
        //   DeepSeek 要求回传上一轮 reasoning_content，缺失会被 API 拒绝，导致工具调用后的回复失败）
        Map<String, Object> deepSeekParams = Map.of("thinking", Map.of("type", "disabled"));
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .apiKey(chatModel.getApiKey())
                .baseUrl(chatModel.getBaseUrl())
                .model(chatModel.getModel())
                .extraBody(deepSeekParams)
                .build();
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .options(options)
                .build();
        //3. 存入上下文（Spring AI 的 OpenAiChatModel 同时实现 ChatModel 与 StreamingChatModel，无需单独构造流式模型）
        dynamicContext.setOpenAiChatModel(openAiChatModel);
        //4. 路由到下一个节点
        log.debug("agent auto assembly chat model node end");
        return router(agentAssemblyCommandEntity, dynamicContext);
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        return agentAssemblyBaseAgentsNode;
    }
}
