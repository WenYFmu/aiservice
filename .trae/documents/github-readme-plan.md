# 计划：为 GitHub 仓库添加 README 文档、项目截图/演示、部署说明

## 项目概况

- **仓库**：`WenYFmu/aiservice`
- **项目名称**：AI Service - 基于 Spring AI 的智能客服系统
- **技术栈**：Spring Boot 3.4.5 + Spring AI 1.0.0 + Alibaba DashScope (通义千问) + Ollama + PGVector + MCP
- **子模块**：`image-search-mcp`（图片搜索 MCP Server）
- **当前状态**：无 README.md，无截图/演示，无部署说明

---

## 实施步骤

### 步骤 1：创建 README.md 文档

在 `/workspace/README.md` 创建完整的 README 文档，包含以下章节：

1. **项目标题与徽章**（Badge 占位符）
   - 项目名称、简介
   - 徽章占位符：Java 版本、Spring Boot 版本、License（待定）

2. **项目简介**
   - 一句话描述：基于 Spring AI 的电商智能客服 Agent 系统
   - 核心功能列表：
     - ReAct Agent 架构（思考-行动循环）
     - RAG 知识库检索增强（PGVector + Ollama Embedding）
     - MCP 工具协议集成（高德地图、图片搜索）
     - 自定义工具（终止交互、询问用户、售后表单、退货订单）
     - 文件持久化对话记忆（Kryo 序列化）
     - SSE 流式响应
     - 百炼平台 Agent 集成
     - Knife4j API 文档

3. **项目截图/演示**（占位符）
   - 架构图占位符
   - 系统交互截图占位符
   - API 文档截图占位符
   - 每个占位符使用标准格式：`<!-- TODO: 替换为实际截图 -->` + 占位图片描述

4. **系统架构**
   - 项目模块说明（主模块 + image-search-mcp 子模块）
   - Agent 架构类图说明：BaseAgent → ReActAgent → ToolCallAgent → ServiceAgent
   - 数据流说明：用户请求 → Controller → Agent/ServiceApp → LLM + Tools + RAG

5. **技术栈**
   - 后端框架、AI 框架、数据库、工具库等详细列表

6. **项目结构**
   - 目录树展示，标注每个模块/包的用途

7. **快速开始 / 部署运行说明**
   - 环境要求（JDK 21+、Maven、PostgreSQL + PGVector 扩展、Ollama）
   - 配置说明（application.yml 关键配置项）
   - 本地开发运行步骤
   - 子模块 image-search-mcp 构建与运行
   - Docker 部署说明（占位符，待补充）

8. **API 接口说明**
   - 列出主要 API 端点及参数

9. **配置项说明**
   - 关键配置项表格（数据库、DashScope API Key、Ollama、MCP 等）

10. **贡献指南**（占位符）

11. **许可证**（占位符）

### 步骤 2：添加项目截图/演示占位符

在 README 中嵌入截图占位符，具体包括：

| 截图类型 | 说明 | 状态 |
|---------|------|------|
| 系统架构图 | 展示 Agent 架构、RAG 流程、MCP 集成 | 占位符，需专业制作 |
| 智能客服对话截图 | 展示 SSE 流式对话效果 | 占位符，需实际运行截图 |
| Agent 执行流程截图 | 展示 ReAct 思考-行动循环 | 占位符，需实际运行截图 |
| Knife4j API 文档截图 | 展示接口文档页面 | 占位符，需实际运行截图 |
| RAG 检索效果截图 | 展示知识库检索增强效果 | 占位符，需实际运行截图 |

占位符格式示例：
```markdown
<!-- 📸 截图占位符：系统架构图 - 需使用专业绘图工具（如 draw.io / Excalidraw）制作 -->
![系统架构图](docs/images/architecture.png)
```

### 步骤 3：添加部署运行说明

详细编写以下部署说明：

1. **环境准备**
   - JDK 21 安装
   - PostgreSQL + PGVector 扩展安装与配置
   - Ollama 安装与模型下载（Qwen3-Embedding-0.6B:F16）
   - Maven 安装

2. **配置修改**
   - `application.yml` 中需要修改的配置项（数据库连接、API Key 等）
   - `mcp-servers.json` 中 MCP 服务器配置说明
   - 强调 API Key 等敏感信息应使用环境变量

3. **构建与运行**
   - 主项目构建命令
   - 子模块构建命令
   - 启动命令
   - 验证服务是否启动成功

4. **Docker 部署**（占位符）
   - Dockerfile 占位符
   - docker-compose.yml 占位符（包含 PostgreSQL + PGVector）

### 步骤 4：使用 gh CLI 推送到 GitHub

1. 创建 README.md 文件
2. 使用 `git add` + `git commit` + `git push` 推送到远程仓库
3. 使用 `gh repo edit` 更新仓库描述

---

## 需要占位符的内容（暂时无法完成）

1. **项目截图**：需要实际运行项目后截图，或使用专业工具制作架构图
2. **Docker 部署配置**：项目目前没有 Dockerfile 和 docker-compose.yml，需要后续补充
3. **License**：项目 pom.xml 中 License 为空，需确认后补充
4. **贡献指南**：需根据项目实际情况编写
5. **徽章（Badge）**：需确认 CI/CD、覆盖率等服务后添加
6. **演示 GIF/视频**：需录制实际运行效果

---

## 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `/workspace/README.md` | 新建 | 完整的项目 README 文档 |
| `/workspace/docs/images/` | 新建目录 | 截图/图片存放目录（占位） |
