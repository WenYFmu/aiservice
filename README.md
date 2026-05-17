# AI Service - 基于 Spring AI 的电商智能客服系统

<!-- TODO: 添加徽章 - 需确认 CI/CD、代码覆盖率等服务后替换 -->
![Java 21](https://img.shields.io/badge/Java-21-blue)
![Spring Boot 3.4.5](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)
![Spring AI 1.0.0](https://img.shields.io/badge/Spring%20AI-1.0.0-orange)
<!-- TODO: 添加 License 徽章 - 需确认项目许可证后替换 -->

基于 Spring AI 框架构建的电商智能客服 Agent 系统，采用 ReAct（思考-行动）架构，集成 RAG 知识库检索增强、MCP 工具协议、对话记忆持久化等能力，支持 SSE 流式响应。

## 核心功能

- **ReAct Agent 架构** — 实现 Think（思考）→ Act（行动）循环，Agent 自主判断是否调用工具，支持多步推理与工具调用
- **RAG 知识库检索增强** — 基于 PGVector + Ollama Embedding，将客服对话记录向量化存储，实现上下文感知的智能检索
- **MCP 工具协议集成** — 通过 Spring AI MCP Client 接入外部工具服务（高德地图、图片搜索等）
- **自定义工具** — 终止交互、询问用户、售后表单、退货订单等电商场景专用工具
- **对话记忆持久化** — 基于 Kryo 序列化的文件存储，支持多会话独立记忆
- **SSE 流式响应** — 服务端推送事件实现实时对话体验
- **百炼平台 Agent 集成** — 支持调用阿里云百炼平台预配置的 Agent 应用
- **API 文档** — 集成 Knife4j (OpenAPI 3) 接口文档

## 项目截图

<!-- 📸 截图占位符：系统架构图 - 需使用专业绘图工具（如 draw.io / Excalidraw）制作 -->
![系统架构图](docs/images/architecture.png)
> 📌 **占位符**：系统架构图 — 展示 Agent 架构、RAG 流程、MCP 集成的整体架构，需使用专业绘图工具制作

<!-- 📸 截图占位符：智能客服对话截图 - 需实际运行项目后截图 -->
![智能客服对话](docs/images/chat-demo.png)
> 📌 **占位符**：智能客服对话截图 — 展示 SSE 流式对话效果，需实际运行项目后截图

<!-- 📸 截图占位符：Agent 执行流程截图 - 需实际运行项目后截图 -->
![Agent 执行流程](docs/images/agent-flow.png)
> 📌 **占位符**：Agent 执行流程截图 — 展示 ReAct 思考-行动循环的日志输出，需实际运行项目后截图

<!-- 📸 截图占位符：Knife4j API 文档截图 - 需实际运行项目后截图 -->
![API 文档](docs/images/knife4j-docs.png)
> 📌 **占位符**：Knife4j API 文档截图 — 展示接口文档页面，需实际运行项目后截图

<!-- 📸 截图占位符：RAG 检索效果截图 - 需实际运行项目后截图 -->
![RAG 检索效果](docs/images/rag-demo.png)
> 📌 **占位符**：RAG 检索效果截图 — 展示知识库检索增强的对话效果，需实际运行项目后截图

## 系统架构

### Agent 架构

项目采用分层 Agent 架构，核心类继承关系如下：

```
BaseAgent (抽象基类 - 状态管理、执行循环、SSE 流式)
  └── ReActAgent (抽象类 - think/act 模式)
        └── ToolCallAgent (工具调用 - LLM 工具判断、ToolCallingManager)
              └── ServiceAgent (电商客服 - 系统提示词、RAG 顾问、工具注册)
```

- **BaseAgent**：管理 Agent 生命周期（IDLE → RUNNING → FINISHED/ERROR），控制最大执行步数，防止死循环
- **ReActAgent**：定义 `think()` 和 `act()` 抽象方法，实现"思考-行动"循环
- **ToolCallAgent**：对接 Spring AI 的 `ToolCallingManager`，实现 LLM 自主判断工具调用
- **ServiceAgent**：面向电商客服场景的具体实现，集成 RAG 顾问和自定义工具

### 数据流

```
用户请求
  │
  ▼
AiServiceController
  │
  ├── /ai/server_app/chat/sync  → ServiceApp.doChatWithRAG()     (同步 RAG 对话)
  ├── /ai/server_app/chat/sse   → ServiceApp.doChatWithMCPSse()  (SSE 流式 MCP+RAG)
  └── /ai/server_app/agent_chat/sse → ServiceAgent.runStream()   (SSE Agent 对话)
        │
        ▼
  ┌─────────────────────────────────────┐
  │           ServiceAgent              │
  │  ┌─────────┐    ┌───────────────┐  │
  │  │  think() │───▶│  DashScope    │  │
  │  │  思考    │    │  (通义千问)    │  │
  │  └─────────┘    └───────┬───────┘  │
  │                         │           │
  │              ┌──────────▼────────┐  │
  │              │    act() 行动     │  │
  │              ├───────────────────┤  │
  │              │ • 自定义工具       │  │
  │              │   - 终止交互       │  │
  │              │   - 询问用户       │  │
  │              │   - 售后表单       │  │
  │              │   - 退货订单       │  │
  │              │ • MCP 工具         │  │
  │              │   - 高德地图       │  │
  │              │   - 图片搜索       │  │
  │              │ • RAG 检索         │  │
  │              │   - PGVector       │  │
  │              └───────────────────┘  │
  └─────────────────────────────────────┘
```

### 模块说明

| 模块 | 说明 |
|------|------|
| `aiservice` (主模块) | Spring Boot 主应用，包含 Agent、RAG、MCP Client、对话记忆等核心功能 |
| `image-search-mcp` (子模块) | 独立的 MCP Server，基于 Pexels API 提供图片搜索工具 |

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.4.5 |
| AI 框架 | Spring AI | 1.0.0 |
| LLM | Alibaba DashScope (通义千问) | spring-ai-alibaba 1.0.0.3 |
| Embedding | Ollama (Qwen3-Embedding-0.6B:F16) | - |
| 向量数据库 | PostgreSQL + PGVector | - |
| MCP | Spring AI MCP Client / Server | 1.0.0 |
| 序列化 | Kryo | 5.6.2 |
| 工具库 | Hutool | 5.8.37 |
| API 文档 | Knife4j (OpenAPI 3) | 4.4.0 |
| 构建工具 | Maven | - |
| Java 版本 | JDK | 21 |

## 项目结构

```
aiservice/
├── src/main/java/com/agent/aiservice/
│   ├── AiServiceApplication.java          # Spring Boot 启动类
│   ├── Agent/                             # Agent 核心架构
│   │   ├── model/AgentState.java          # Agent 状态枚举 (IDLE/RUNNING/FINISHED/ERROR)
│   │   ├── BaseAgent.java                 # Agent 抽象基类 (执行循环、SSE 流式)
│   │   ├── ReActAgent.java               # ReAct 模式抽象类 (think/act)
│   │   ├── ToolCallAgent.java            # 工具调用 Agent (ToolCallingManager)
│   │   └── ServiceAgent.java             # 电商客服 Agent (系统提示词、RAG、工具)
│   ├── ServiceAgent/                      # ServiceApp 对话服务
│   │   └── ServiceApp.java               # 基于 ChatClient 的对话服务 (RAG/MCP/记忆)
│   ├── advisor/                           # 顾问 (Advisor)
│   │   ├── MyLoggerAdvisor.java          # 自定义日志顾问
│   │   └── RAGAdvisor.java              # RAG 检索增强顾问配置
│   ├── chatmemory/                        # 对话记忆
│   │   └── FileBasedChatMemory.java      # 基于 Kryo 的文件持久化对话记忆
│   ├── controller/                        # 控制器
│   │   ├── AiServiceController.java      # AI 服务接口 (同步/SSE/Agent)
│   │   └── BailianAgentController.java   # 百炼平台 Agent 接口
│   ├── cors/                              # 跨域配置
│   │   └── CorsConfig.java              # 全局 CORS 配置
│   ├── invoke/                            # 调用示例
│   │   └── testAlibaba.java             # 阿里云调用测试
│   ├── once/                              # 一次性任务
│   │   └── LoadDocumentToPGvector.java   # 文档加载到 PGVector
│   ├── prompt/                            # 提示词模板
│   │   └── MyPromptTemplate.java         # 自定义提示词模板
│   ├── rag/                               # RAG 检索增强
│   │   ├── QueryRewriter.java            # 查询重写
│   │   ├── ServiceAppDocumentLoader.java # JSON 文档加载器
│   │   ├── ServiceAppOllamaEmbedding.java# Ollama Embedding 配置
│   │   └── ServiceAppVectorStoreConfig.java # 向量存储配置
│   └── tools/                             # 自定义工具
│       ├── ToolRegistration.java         # 工具统一注册
│       ├── TerminateTool.java            # 终止交互工具
│       └── AskHumanTool.java             # 询问用户工具
├── src/main/resources/
│   ├── application.yml                    # 主配置文件
│   └── mcp-servers.json                  # MCP 服务器配置
├── image-search-mcp/                      # 图片搜索 MCP Server 子模块
│   ├── src/main/java/com/agent/imagesearchmcp/
│   │   ├── ImageSearchMcpApplication.java # MCP Server 启动类
│   │   └── tools/ImgSearchTool.java      # 图片搜索工具 (Pexels API)
│   └── src/main/resources/
│       ├── application.yml               # 主配置
│       ├── application-sse.yml           # SSE 模式配置
│       └── application-stdio.yml         # STDIO 模式配置
├── tmp/chat-memory/                       # 对话记忆文件存储目录
└── pom.xml                               # Maven 项目配置
```

## 快速开始

### 环境要求

| 依赖 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 21+ | 必须 |
| Maven | 3.8+ | 构建 |
| PostgreSQL | 14+ | 需安装 PGVector 扩展 |
| Ollama | 最新 | 用于本地 Embedding 模型 |
| Node.js | 18+ | MCP Server (高德地图) 运行需要 npx |

### 1. 安装 PostgreSQL + PGVector

```bash
# 安装 PostgreSQL (以 Ubuntu 为例)
sudo apt install postgresql postgresql-contrib

# 安装 PGVector 扩展
# 参考: https://github.com/pgvector/pgvector
cd /tmp
git clone --branch v0.7.0 https://github.com/pgvector/pgvector.git
cd pgvector
make
sudo make install

# 创建数据库并启用扩展
sudo -u postgres psql
CREATE DATABASE postgres;
\c postgres
CREATE EXTENSION vector;
```

### 2. 安装 Ollama 并下载 Embedding 模型

```bash
# 安装 Ollama
# 参考: https://ollama.com/install
curl -fsSL https://ollama.com/install.sh | sh

# 下载 Embedding 模型
ollama pull dengcao/Qwen3-Embedding-0.6B:F16
```

### 3. 配置应用

编辑 `src/main/resources/application.yml`，修改以下配置项：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres    # PostgreSQL 连接地址
    username: postgres                                 # 数据库用户名
    password: your_password                            # 数据库密码
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}                   # 建议使用环境变量
      agent:
        app-id: ${DASHSCOPE_AGENT_APP_ID}             # 百炼 Agent ID (可选)
```

> ⚠️ **安全提示**：请勿将 API Key 等敏感信息硬编码在配置文件中，建议使用环境变量：
> ```bash
> export DASHSCOPE_API_KEY=your_api_key
> export DASHSCOPE_AGENT_APP_ID=your_app_id
> ```

### 4. 构建项目

```bash
# 构建主项目
./mvnw clean package -DskipTests

# 构建图片搜索 MCP Server 子模块
cd image-search-mcp
../mvnw clean package -DskipTests
cd ..
```

### 5. 加载知识库文档到 PGVector

首次运行前，需要将客服对话记录加载到向量数据库。可通过 `LoadDocumentToPGvector` 类执行一次性加载（需确保 `classpath:document/train.json` 文件存在）。

### 6. 启动应用

```bash
./mvnw spring-boot:run
```

服务启动后访问：
- 应用地址：`http://localhost:8081/api`
- API 文档：`http://localhost:8081/api/swagger-ui.html`（账号：`shamu` / 密码：`aiservice`）

### 7. MCP Server 配置说明

`src/main/resources/mcp-servers.json` 配置了两个 MCP Server：

```json
{
  "mcpServers": {
    "amap-maps": {
      "command": "npx",
      "args": ["-y", "@amap/amap-maps-mcp-server"],
      "env": {
        "AMAP_MAPS_API_KEY": "your_amap_api_key"
      }
    },
    "image-search-mcp-server": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "image-search-mcp/target/image-search-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

> ⚠️ 使用高德地图 MCP Server 需要先安装 Node.js，并替换 `AMAP_MAPS_API_KEY` 为你自己的高德地图 API Key。

### Docker 部署

<!-- TODO: Docker 部署配置 - 需创建 Dockerfile 和 docker-compose.yml 后补充 -->

> 📌 **占位符**：Docker 部署方案待补充，计划包含：
> - `Dockerfile` — 多阶段构建，JDK 21 基础镜像
> - `docker-compose.yml` — 包含 PostgreSQL + PGVector、Ollama、主应用服务
>
> 如需 Docker 部署，请参考以下思路：
> ```yaml
> # docker-compose.yml 示例结构 (待完善)
> services:
>   postgres:
>     image: pgvector/pgvector:pg16
>     environment:
>       POSTGRES_PASSWORD: your_password
>   ollama:
>     image: ollama/ollama
>   aiservice:
>     build: .
>     depends_on:
>       - postgres
>       - ollama
>     environment:
>       DASHSCOPE_API_KEY: ${DASHSCOPE_API_KEY}
> ```

## API 接口说明

### 智能客服对话

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/server_app/chat/sync` | GET | 同步 RAG 对话 |
| `/api/ai/server_app/chat/sse` | GET | SSE 流式对话 (MCP + RAG) |
| `/api/ai/server_app/agent_chat/sse` | GET | SSE 流式 Agent 对话 |

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `query` | String | 是 | 用户输入内容 |
| `chatId` | String | 是* | 会话 ID（sync/sse 接口必填，agent_chat 不需要） |

#### 请求示例

```bash
# 同步对话
curl "http://localhost:8081/api/ai/server_app/chat/sync?query=你好&chatId=test-001"

# SSE 流式对话
curl -N "http://localhost:8081/api/ai/server_app/chat/sse?query=你好&chatId=test-001"

# Agent 流式对话
curl -N "http://localhost:8081/api/ai/server_app/agent_chat/sse?query=我想退货"
```

### 百炼平台 Agent

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/app/bailian/agent/call` | GET | 调用百炼平台预配置 Agent |

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `message` | String | 否 | 发送给 Agent 的消息（默认：如何使用SDK快速调用阿里云百炼的应用?） |

## 配置项说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | `8081` | 服务端口 |
| `server.servlet.context-path` | `/api` | 上下文路径 |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/postgres` | PostgreSQL 连接地址 |
| `spring.ai.dashscope.api-key` | - | 阿里云 DashScope API Key |
| `spring.ai.dashscope.agent.app-id` | - | 百炼平台 Agent 应用 ID |
| `spring.ai.dashscope.chat.options.temperature` | `0.3` | 对话温度参数 |
| `spring.ai.ollama.embedding.model` | `dengcao/Qwen3-Embedding-0.6B:F16` | Ollama Embedding 模型 |
| `spring.ai.vectorstore.pgvector.index-type` | `HNSW` | PGVector 索引类型 |
| `spring.ai.vectorstore.pgvector.distance-type` | `COSINE_DISTANCE` | 向量距离计算方式 |
| `spring.ai.vectorstore.pgvector.dimensions` | `1024` | 向量维度 |
| `spring.ai.mcp.client.stdio.servers-configuration` | `classpath:mcp-servers.json` | MCP 服务器配置文件路径 |
| `knife4j.basic.username` | `shamu` | API 文档访问账号 |
| `knife4j.basic.password` | `aiservice` | API 文档访问密码 |

## 贡献指南

<!-- TODO: 贡献指南 - 需根据项目实际情况补充 -->

> 📌 **占位符**：贡献指南待补充，建议包含：
> - 代码规范
> - 提交信息格式
> - PR 流程
> - 开发环境搭建

## 许可证

<!-- TODO: 许可证 - 需确认项目许可证后补充 -->

> 📌 **占位符**：项目许可证待确认，pom.xml 中 License 字段当前为空。
