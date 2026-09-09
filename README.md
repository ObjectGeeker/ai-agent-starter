# AI Agent Starter

> 基于 **Spring AI + Google ADK** 的声明式 AI Agent 应用脚手架。通过一份 YAML 配置即可完成单智能体 / 多智能体的自动装配，开箱即用地提供对话接口与 Web 调试界面。

---

## 一、项目简介

`ai-agent-starter` 是一个 AI Agent 应用的启动脚手架（Starter）。它把 **Spring AI**（模型调用）、**Google ADK**（智能体编排）与 **MCP**（工具生态）统一封装，屏蔽了底层模型接入、工具编排、多智能体协作等复杂细节。

开发者只需编写声明式的 YAML 配置，即可在应用启动时自动装配出可用的 Agent，并对外暴露标准的对话 REST 接口，无需硬编码 Agent 构建逻辑。

## 二、解决了什么问题

| 痛点 | 本项目的解法 |
| --- | --- |
| 搭建 Agent 应用需要大量样板代码，模型/工具/编排耦合在一起 | **声明式装配**：一份 YAML 描述 Agent，启动时自动构建，业务零硬编码 |
| 单智能体、多智能体协作（顺序/并行/循环）实现方式各异，难以统一 | **统一编排模型**：基于 Google ADK 抽象出 loop / parallel / sequential 三类协作节点 |
| 工具接入方式多样（MCP、本地函数、技能包），缺乏统一入口 | **三类工具扩展**：MCP 工具（SSE/Stdio）、Function Tool（Spring Bean）、Skills（技能目录） |
| 流式对话（SSE）实现繁琐，前后端联调成本高 | **内置流式接口 + Web 调试台**：开箱即用的对话页面，支持 Markdown 渲染与工具调用可视化 |
| 项目缺乏清晰分层，后期难以维护扩展 | **DDD 分层 + 策略路由树**：装配流程以责任链节点组织，职责清晰、易于扩展 |

## 三、现有功能

- **声明式 Agent 装配**：应用启动后读取 YAML 配置表，自动完成 Agent 构建与注册。
- **单智能体**：基于 `LlmAgent`，支持自定义指令（instruction）、输出 Key、输出 Schema。
- **多智能体协作**：
  - `sequential`：子智能体按顺序依次执行
  - `parallel`：子智能体并行执行
  - `loop`：子智能体循环执行直至满足退出条件（可配置 `maxIterations`）
- **工具生态**：
  - **MCP 工具**：支持 SSE 与 Stdio 两种传输方式
  - **Function Tool**：将 Spring Bean 中带 `@Annotations.Schema` 注解的方法暴露为工具
  - **Skills**：支持 `classpath`（工程内资源）与 `directory`（外部目录）两种技能来源
- **对话能力**：
  - 普通对话（阻塞式，一次性返回完整回复）
  - 流式对话（SSE，逐帧推送 AI 文本与工具调用事件）
  - 会话管理（内存态 Session）
- **Web 调试台**：内置 `index.html`，支持 Agent 选择、Markdown 渲染、代码高亮、工具调用卡片可视化、流式/非流式双模式。
- **多模型兼容**：基于 Spring AI OpenAI 兼容协议，可对接 OpenAI、DeepSeek 等任意兼容 OpenAI 接口的大模型。

## 四、技术栈

| 分类 | 技术 | 版本 |
| --- | --- | --- |
| 语言 / 运行时 | Java | 21 |
| 应用框架 | Spring Boot | 3.5.16 |
| AI 模型接入 | Spring AI（openai / mcp） | 2.0.1 |
| 智能体编排 | Google ADK（google-adk-spring-ai） | 1.9.0 |
| 设计模式框架 | xfg-wrench-starter-design-framework（策略路由树） | 3.3.0 |
| 通用能力封装 | brick-spring-boot-starter（统一响应 / 异常） | 1.2 |
| 工具库 | Hutool / Lombok | 5.8.47 / 1.18.46 |

## 五、架构设计

### 5.1 DDD 分层

```
com.zephyr.ai
├── api             接口层：Controller、请求/响应 DTO
├── app             应用层：自动装配配置（AiAgentAutoConfig）
├── domain          领域层：Agent 模型（entity/valobj）与领域服务（assembly/chat）
├── infrastructure  基础设施层：AgentRegistry 的 Spring 容器实现
└── types           通用类型层
```

> 领域层通过 `AgentRegistry` 接口与基础设施解耦，不直接依赖 Spring 容器；所有 Bean 均采用**构造器注入**。

### 5.2 装配流程（策略路由树）

Agent 装配以责任链节点组织，每个节点完成一项职责后路由到下一节点：

```
Root（根节点）
  └─> ChatModel（构造 Spring AI OpenAiChatModel）
        └─> SubAgents（构造 MCP/Function/Skill 工具 + LlmAgent）
              └─> MultiAgent（中转：按类型分发）
                    ├─> Sequential / Parallel / Loop（构造多智能体，回环至 MultiAgent）
                    └─> Runner（组装 InMemoryRunner 并注册到 AgentRegistry）
```

## 六、快速开始

### 6.1 环境要求

- JDK 21+
- Maven 3.9+

### 6.2 配置模型密钥

复制 `.env.example` 为 `.env`，填入你的模型服务信息：

```properties
APIKEY=sk-xxxxxxxxxxxxxxxx
BASEURL=https://api.deepseek.com
MODEL_NAME=deepseek-chat
```

> `application.yml` 已通过 `spring.config.import` 自动加载 `.env`，`.env` 不纳入版本控制。

### 6.3 配置 Agent

在 `src/main/resources/agent/` 下编辑 Agent 配置表（参考 `agent-dev.yml` / `agent-example.yml`），并在 `application.yml` 中确保 `agent.auto.config.enable=true`。

### 6.4 启动应用

```bash
mvn spring-boot:run
```

- 服务端口：`8123`，接口前缀：`/api`
- Web 调试台：<http://localhost:8123/api/index.html>
- 流式模式调试台：<http://localhost:8123/api/index.html?streamMode=true>

## 七、配置说明

Agent 配置位于 `agent.auto.config.config-table-map` 下，每一项对应一个可对话的 Agent 应用：

```yaml
agent:
  auto:
    config:
      enable: true                    # 是否启用自动装配
      config-table-map:
        daily-agent:                  # 配置表名（任意）
          appName: "单智能体应用"
          agent:                      # Agent 元信息
            agentId: "agent-single-001"
            agentName: "客服助手"
            agentDesc: "处理常见客户咨询"
          module:
            chatModel:                # 模型配置
              baseUrl: ${BASEURL}
              apiKey: ${APIKEY}
              model: ${MODEL_NAME}
            agents:                   # 子智能体列表
              - name: "customer_service"
                instruction: "你是一位专业的客服代表，请友好地解答用户问题。"
                description: "客服智能体"
                toolMcpList: [ ]      # MCP 工具（SSE / Stdio）
            multiAgents: [ ]          # 多智能体协作（sequential / parallel / loop）
            runner:                   # 运行入口
              agent-name: customer_service
```

<details>
<summary>多智能体 + 工具配置示例（点击展开）</summary>

```yaml
module:
  agents:
    - name: "planner"
      instruction: "你是一位内容规划师，负责生成文章大纲。"
      outputKey: "outline"
      toolMcpList:
        - sse:
            name: "search-sse"
            baseUri: "http://search-service"
            sseEndpoint: "/events"
            requestTimeout: 5000
    - name: "writer"
      instruction: "你是一位专业作家，根据大纲撰写详细内容。"
      toolMcpList:
        - stdio:
            name: "file-stdio"
            serverParameters:
              command: "python"
              args: [ "/path/to/writer_tool.py" ]
  multiAgents:
    - type: "sequential"          # 顺序执行
      name: "文章生成流水线"
      subAgents: [ "planner", "writer" ]
      maxIterations: 3
```

</details>

## 八、API 接口

统一前缀 `/api`，响应格式 `{ code, message, data }`（`code === 0` 表示成功）。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/agent/query_agent_list` | 查询可用 Agent 列表 |
| GET | `/api/agent/create_session?userId=&agentId=` | 创建会话，返回 `sessionId` |
| POST | `/api/agent/chat` | 非流式对话，一次性返回完整回复 |
| POST | `/api/agent/stream_chat` | 流式对话（SSE），逐帧推送 AI 文本与工具调用事件 |

**对话请求体：**

```json
{
  "userId": "user-001",
  "agentId": "agent-single-001",
  "sessionId": "xxx",
  "userMessage": "你好"
}
```

## 九、项目结构

```
src/main/java/com/zephyr/ai
├── Application.java                         启动入口
├── api/                                     接口层
│   ├── AgentChatController.java             对话接口
│   └── model/{request,response}/            请求/响应 DTO
├── app/config/
│   └── AiAgentAutoConfig.java               应用就绪后触发自动装配
├── domain/agent/
│   ├── model/{entity,valobj}/               领域模型（命令实体、配置表、注册信息、枚举）
│   └── service/
│       ├── AgentRegistry.java               Agent 注册表接口（领域层）
│       ├── assembly/                        装配服务 + 策略路由节点
│       │   ├── factory/                     装配工厂与上下文
│       │   ├── mcp/client/                  MCP 客户端创建（SSE / Stdio）
│       │   └── node/                        各装配节点（含 multiagent 子节点）
│       └── chat/                            对话服务
└── infrastructure/agent/
    └── SpringAgentRegistry.java             基于 Spring 容器的注册表实现

src/main/resources
├── application.yml                          主配置
├── agent/                                   Agent 配置表 + Skills 资源
└── static/index.html                        Web 调试台
```

## 十、未来规划

| 方向 | 说明 |
| --- | --- |
| **接口文档（Knife4j）** | 引入 Knife4j 增强 Swagger，提供可视化、可调试的在线 API 文档。 |
| **权限管理** | 增加用户认证与鉴权（如 JWT + RBAC），对 Agent 与接口进行访问控制。 |
| **接口限流** | 对对话等高频/高成本接口做限流（如令牌桶 / 滑动窗口），保障服务稳定性。 |
| **多模态** | 支持图片、音频等多模态输入输出，扩展 Agent 的感知与表达能力。 |
| **文件上传管理** | 提供文件上传、存储与管理能力，支撑多模态与知识库类场景。 |

---

## 许可证

本项目仅用于学习与脚手架参考。
