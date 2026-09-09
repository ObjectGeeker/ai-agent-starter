package com.zephyr.ai.domain.agent.model.valobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Agent 配置表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAgentConfigTableVO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 智能体配置
     */
    private Agent agent;

    /**
     * 智能体模块
     */
    private Module module;

    @Data
    public static class Agent {

        /**
         * 智能体ID
         */
        private String agentId;

        /**
         * 智能体名称
         */
        private String agentName;

        /**
         * 智能体描述
         */
        private String agentDesc;

    }

    @Data
    public static class Module {

        private ChatModel chatModel;

        private List<Agent> agents;

        private List<MultiAgent> multiAgents;

        private Runner runner;

        @Data
        public static class ChatModel {
            /**
             * 隐私信息不序列化，避免日志打印泄露
             */
            @JsonIgnore
            private String baseUrl;
            @JsonIgnore
            private String apiKey;
            private String completionsPath = "/v1/chat/completions";
            private String embeddingsPath = "/v1/embeddings";
            private String model;
        }

        @Data
        public static class Agent {
            private String name;
            private String instruction;
            private String description;
            private String outputKey;
            private String outputSchema;

            private FunctionTool functionTool;

            private List<ToolMcp> toolMcpList;

            private List<ToolSkills> toolSkillsList;

            @Data
            public static class ToolMcp {

                private SSEServerParameters sse;

                private StdioServerParameters stdio;

                @Data
                public static class SSEServerParameters {
                    private String name;
                    private String baseUri;
                    private String sseEndpoint;
                    private Integer requestTimeout = 3000;

                }

                @Data
                public static class StdioServerParameters {
                    private String name;
                    private Integer requestTimeout = 3000;
                    private ServerParameters serverParameters;

                    @Data
                    public static class ServerParameters {
                        private String command;
                        private List<String> args;
                        private Map<String, String> env;

                    }
                }

            }

            @Data
            public static class FunctionTool {
                private List<String> beanNameList;
            }

            @Data
            public static class ToolSkills {
                /**
                 * 类型；directory（用户配置的，映射进来的）、resource（放到工程下的）
                 */
                private String type = "directory";
                /**
                 * 路径；
                 */
                private String path;
            }
        }

        @Data
        public static class MultiAgent {
            /**
             * 类型；loop、parallel、sequential
             */
            private String type;
            private String name;
            private List<String> subAgents;
            private String description;
            private Integer maxIterations = 3;

        }

        @Data
        public static class Runner {

            private String agentName;

            private List<String> pluginNames;

        }
    }

}
