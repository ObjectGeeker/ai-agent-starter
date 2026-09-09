package com.zephyr.ai.domain.agent.service.assembly.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.springai.SpringAI;
import com.google.adk.skills.ClassPathSkillSource;
import com.google.adk.skills.LocalSkillSource;
import com.google.adk.tools.Annotations;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.FunctionTool;
import com.google.adk.tools.skills.SkillToolset;
import com.google.genai.types.Schema;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.agent.model.entity.AgentAssemblyCommandEntity;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.zephyr.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.zephyr.ai.domain.agent.model.valobj.SkillResourceEnum;
import com.zephyr.ai.domain.agent.service.AgentRegistry;
import com.zephyr.ai.domain.agent.service.assembly.AbstractAgentAssemblySupport;
import com.zephyr.ai.domain.agent.service.assembly.factory.DefaultAgentAssemblyFactory;
import com.zephyr.ai.domain.agent.service.assembly.mcp.client.factory.DefaultMcpClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 子智能体装配节点：遍历 agents 配置，构造 MCP 工具、FunctionTool、Skills 并组装 LlmAgent
 */
@Component
@Slf4j
public class AgentAssemblyBaseAgentsNode extends AbstractAgentAssemblySupport {

    private final DefaultMcpClientFactory defaultMcpClientFactory;

    private final AgentAssemblyMultiAgentNode agentAssemblyMultiAgentNode;

    private final AgentRegistry agentRegistry;

    public AgentAssemblyBaseAgentsNode(DefaultMcpClientFactory defaultMcpClientFactory, AgentAssemblyMultiAgentNode agentAssemblyMultiAgentNode, AgentRegistry agentRegistry) {
        this.defaultMcpClientFactory = defaultMcpClientFactory;
        this.agentAssemblyMultiAgentNode = agentAssemblyMultiAgentNode;
        this.agentRegistry = agentRegistry;
    }

    @Override
    protected AiAgentRegisterVO doApply(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        //1. 校验数据
        List<AiAgentConfigTableVO.Module.Agent> agents = agentAssemblyCommandEntity.getConfigTable().getModule().getAgents();
        if (null == agents) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "agent auto assembly agent node empty !");
        }

        for (AiAgentConfigTableVO.Module.Agent agent : agents) {
            List<AiAgentConfigTableVO.Module.Agent.ToolMcp> toolMcpList = agent.getToolMcpList();
            //2. 构造工具列表（LlmAgent.tools() 接受 List<?>，可同时容纳 BaseToolset 与 FunctionTool）
            List<Object> toolList = new ArrayList<>();
            if (CollUtil.isNotEmpty(toolMcpList)) {
                for (AiAgentConfigTableVO.Module.Agent.ToolMcp toolMcp : toolMcpList) {
                    toolList.addAll(defaultMcpClientFactory.getToolMcpCreateService(toolMcp).buildMcpToolSet(toolMcp));
                }
            }
            //3. 构造functionTool列表
            if (agent.getFunctionTool() != null && CollUtil.isNotEmpty(agent.getFunctionTool().getBeanNameList())) {
                toolList.addAll(buildFunctionToolList(agent.getFunctionTool().getBeanNameList()));
            }

            //4. 构造Skills列表
            List<AiAgentConfigTableVO.Module.Agent.ToolSkills> toolSkillsList = agent.getToolSkillsList();
            if (CollUtil.isNotEmpty(toolSkillsList)) {
                for (AiAgentConfigTableVO.Module.Agent.ToolSkills toolSkills : toolSkillsList) {
                    toolList.add(buildSkillToolSet(toolSkills));
                }
            }

            //5. 构造LlmAgent（Spring AI 的 OpenAiChatModel 同时实现 ChatModel 与 StreamingChatModel，传入同一实例即可）
            LlmAgent llmAgent = LlmAgent.builder()
                    .model(new SpringAI(
                            dynamicContext.getOpenAiChatModel(),
                            dynamicContext.getOpenAiChatModel(),
                            agentAssemblyCommandEntity.getConfigTable().getModule().getChatModel().getModel()))
                    .name(agent.getName())
                    .description(agent.getDescription())
                    .instruction(agent.getInstruction())
                    .outputKey(agent.getOutputKey())
                    .outputSchema(agent.getOutputSchema() != null ? Schema.fromJson(agent.getOutputSchema()) : null)
                    .tools(toolList)
                    .build();

            dynamicContext.getBaseAgentMap().put(llmAgent.name(), llmAgent);
        }

        //6. 路由到下一个节点
        return router(agentAssemblyCommandEntity, dynamicContext);
    }

    /**
     * 根据 Skill 资源类型构造 SkillToolset
     *
     * @param toolSkills Skill 配置
     * @return SkillToolset 实例
     */
    private BaseToolset buildSkillToolSet(AiAgentConfigTableVO.Module.Agent.ToolSkills toolSkills) {
        if (SkillResourceEnum.classpath.name().equals(toolSkills.getType())) {
            return new SkillToolset(new ClassPathSkillSource(toolSkills.getPath()));
        }
        if (SkillResourceEnum.directory.name().equals(toolSkills.getType())) {
            return new SkillToolset(new LocalSkillSource(Path.of(toolSkills.getPath())));
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "skill type not support !");
    }

    /**
     * 根据 Bean 名称列表构造 FunctionTool 列表：扫描 Bean 中带 @Annotations.Schema 注解的方法
     *
     * @param beanNameList 工具 Bean 名称列表
     * @return FunctionTool 列表
     */
    private List<FunctionTool> buildFunctionToolList(List<String> beanNameList) {
        List<FunctionTool> functionTools = new ArrayList<>();
        //1. 从容器中获取bean
        for (String beanName : beanNameList) {
            //2. 扫描bean的方法
            Object toolBean = agentRegistry.lookupBean(beanName);
            Method[] methods = ReflectUtil.getMethods(toolBean.getClass());
            for (Method method : methods) {
                //3. 获取加了注解的方法
                if (method.isAnnotationPresent(Annotations.Schema.class)) {
                    //4. 构造FunctionTool
                    functionTools.add(FunctionTool.create(toolBean.getClass(), method.getName()));
                }
            }
        }
        return functionTools;
    }

    @Override
    public StrategyHandler<AgentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext, AiAgentRegisterVO> get(AgentAssemblyCommandEntity agentAssemblyCommandEntity, DefaultAgentAssemblyFactory.DynamicContext dynamicContext) throws Exception {
        return agentAssemblyMultiAgentNode;
    }
}
