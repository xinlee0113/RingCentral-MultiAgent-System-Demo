# RingCentral MultiAgent System

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xinlee0113_RingCentral-MultiAgent-System-Demo&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xinlee0113_RingCentral-MultiAgent-System-Demo)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xinlee0113_RingCentral-MultiAgent-System-Demo&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xinlee0113_RingCentral-MultiAgent-System-Demo)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=xinlee0113_RingCentral-MultiAgent-System-Demo&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=xinlee0113_RingCentral-MultiAgent-System-Demo)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=xinlee0113_RingCentral-MultiAgent-System-Demo&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=xinlee0113_RingCentral-MultiAgent-System-Demo)

## 🎯 项目概述

RingCentral企业级AI智能体协同平台是一个基于Spring Boot和微服务架构的现代化企业通信解决方案。

## 🔧 SonarCloud代码质量分析

本项目已集成SonarCloud进行持续代码质量分析，包括：

- 🔍 **代码质量检测**: 检测代码异味、技术债务
- 🐛 **Bug检测**: 发现潜在的运行时错误
- 🔒 **安全漏洞扫描**: 识别安全风险和漏洞
- 📊 **测试覆盖率**: 统计和监控测试覆盖率
- 📈 **质量趋势**: 跟踪代码质量变化趋势

### 查看分析报告
访问 [SonarCloud项目页面](https://sonarcloud.io/project/overview?id=xinlee0113_RingCentral-MultiAgent-System-Demo) 查看详细的代码质量报告。

## 🚀 快速开始

### 环境要求
- Java 17+
- Docker & Docker Compose
- Gradle 8.13+

### 本地开发
```bash
# 克隆项目
git clone https://github.com/xinlee0113/RingCentral-MultiAgent-System-Demo.git

# 进入项目目录
cd RingCentral-MultiAgent-System-Demo

# 构建项目
./gradlew build

# 运行代码质量分析（需要配置SonarCloud token）
./gradlew sonarqube
```

## 📊 CI/CD流水线

项目采用GitHub Actions进行持续集成，包括：

1. **代码质量检查** - Spotless、Detekt
2. **快速构建** - 14个服务并行构建
3. **单元测试** - JUnit 5 + Mockito
4. **集成测试** - Testcontainers
5. **代码质量分析** - SonarCloud
6. **Docker镜像构建** - Jib + GitHub Container Registry
7. **Kubernetes部署** - Kind本地集群

## 🏗️ 架构设计

### 微服务架构
- **平台服务**: API Gateway, Auth Service, Config Service, Monitor Service
- **智能体服务**: Meeting Agent, Call Agent, Router Agent, Analytics Agent  
- **AI引擎**: Speech Engine, NLU Engine, Knowledge Engine, Reasoning Engine

### 技术栈
- **后端**: Spring Boot 3.2, Spring Cloud 2023.0
- **数据库**: PostgreSQL, Redis
- **消息队列**: Apache Kafka
- **AI/ML**: LangChain4j, OpenAI API
- **监控**: Micrometer, Prometheus, Zipkin
- **容器化**: Docker, Kubernetes
- **代码质量**: SonarCloud, JaCoCo

## 📈 代码质量标准

项目遵循严格的代码质量标准：
- 测试覆盖率 ≥ 80%
- 代码重复率 < 3%
- 安全漏洞 = 0
- 代码异味密度 < 0.5%

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

所有Pull Request都会自动触发SonarCloud分析，确保代码质量。

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

**🎉 SonarCloud代码质量分析已启用！** 每次提交都会自动进行代码质量检查。