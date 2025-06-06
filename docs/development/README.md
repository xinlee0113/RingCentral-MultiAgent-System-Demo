# 💻 开发文档

本目录包含RingCentral多智能体系统的开发相关文档。

## 📋 文档规划

### 🛠️ 开发环境搭建
- **IDE配置** - IntelliJ IDEA/VS Code配置
- **本地环境** - 开发环境搭建指南
- **依赖管理** - Gradle配置和依赖管理
- **数据库配置** - 本地数据库搭建

### 📝 编码规范
- **Java编码规范** - Java代码风格指南
- **Kotlin编码规范** - Kotlin代码风格指南
- **命名约定** - 类、方法、变量命名规范
- **注释规范** - 代码注释和文档规范

### 🧪 测试指南
- **单元测试** - JUnit5测试编写指南
- **集成测试** - Spring Boot集成测试
- **端到端测试** - E2E测试框架使用
- **性能测试** - JMeter/Gatling性能测试

### 🐛 调试指南
- **本地调试** - IDE调试技巧
- **远程调试** - 生产环境调试
- **日志分析** - 日志查看和分析
- **性能分析** - 性能瓶颈定位

## 🚀 快速开始

### 环境准备
```bash
# 1. 安装Java 17
java -version

# 2. 克隆项目
git clone <repository-url>
cd RingCentral-MultiAgent-System

# 3. 构建项目
./gradlew build

# 4. 启动本地服务
./gradlew bootRun
```

### IDE配置
```bash
# IntelliJ IDEA
# 1. 导入项目 (Import Project)
# 2. 选择Gradle项目
# 3. 配置JDK 17
# 4. 安装Lombok插件
# 5. 启用注解处理
```

## 🔧 开发工具

### 必需工具
- **Java JDK 17+** - 开发运行环境
- **IntelliJ IDEA** - 推荐IDE
- **Git** - 版本控制
- **Docker Desktop** - 容器化开发

### 推荐插件
- **Lombok** - 减少样板代码
- **SonarLint** - 代码质量检查
- **GitToolBox** - Git增强工具
- **Rainbow Brackets** - 括号高亮

### 开发框架
- **Spring Boot 3.2.0** - 应用框架
- **Spring Cloud 2023.0.0** - 微服务框架
- **JUnit 5** - 测试框架
- **Mockito** - Mock框架

## 📊 项目结构

### 模块组织
```
RingCentral-MultiAgent-System/
├── shared/                    # 共享组件
│   ├── src/main/java/        # Java源码
│   ├── src/main/kotlin/      # Kotlin源码
│   └── src/test/             # 测试代码
├── infrastructure/           # 基础设施
├── platform-services/       # 平台服务
├── ai-engines/              # AI引擎
├── agent-services/          # 智能体服务
├── tests/                   # 测试模块
├── docs/                    # 文档
├── scripts/                 # 脚本
└── gradle/                  # Gradle配置
```

### 代码组织
```java
com.ringcentral.multiagent/
├── controller/              # 控制器层
├── service/                # 服务层
├── repository/             # 数据访问层
├── model/                  # 数据模型
├── config/                 # 配置类
├── exception/              # 异常处理
├── util/                   # 工具类
└── integration/            # 外部集成
```

## 📝 编码规范

### Java规范
```java
// 类命名：大驼峰命名法
public class UserService {
    
    // 常量：全大写，下划线分隔
    private static final String DEFAULT_USER_NAME = "anonymous";
    
    // 方法命名：小驼峰命名法
    public User findUserById(Long userId) {
        // 方法实现
    }
    
    // 变量命名：小驼峰命名法
    private String userName;
}
```

### Kotlin规范
```kotlin
// 类命名：大驼峰命名法
class UserService {
    
    // 常量：全大写，下划线分隔
    companion object {
        const val DEFAULT_USER_NAME = "anonymous"
    }
    
    // 方法命名：小驼峰命名法
    fun findUserById(userId: Long): User {
        // 方法实现
    }
    
    // 属性命名：小驼峰命名法
    private val userName: String = ""
}
```

## 🧪 测试策略

### 测试金字塔
```
        /\
       /  \
      / E2E \     <- 少量端到端测试
     /______\
    /        \
   /Integration\ <- 适量集成测试
  /__________\
 /            \
/  Unit Tests  \   <- 大量单元测试
/______________\
```

### 测试覆盖率目标
- **单元测试覆盖率**: > 80%
- **集成测试覆盖率**: > 60%
- **端到端测试覆盖率**: > 40%

### 测试命令
```bash
# 运行所有测试
./gradlew test

# 运行特定模块测试
./gradlew :shared:test

# 生成覆盖率报告
./gradlew jacocoTestReport

# 运行集成测试
./gradlew :tests:integration-tests:test
```

## 🐛 调试技巧

### 本地调试
```bash
# 启动调试模式
./gradlew bootRun --debug-jvm

# 或者设置JVM参数
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
./gradlew bootRun
```

### 远程调试
```bash
# 连接远程调试端口
# IDE中配置Remote Debug，端口5005
```

### 日志配置
```yaml
# application.yml
logging:
  level:
    com.ringcentral.multiagent: DEBUG
    org.springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 📊 性能优化

### 代码优化
- **避免N+1查询** - 使用JOIN或批量查询
- **合理使用缓存** - Redis缓存热点数据
- **异步处理** - 使用@Async处理耗时操作
- **连接池优化** - 配置合适的连接池大小

### JVM优化
```bash
# 生产环境JVM参数
-Xmx4g -Xms4g
-XX:+UseG1GC
-XX:+UseStringDeduplication
-XX:+HeapDumpOnOutOfMemoryError
```

## 🔄 开发流程

### Git工作流
```bash
# 1. 创建功能分支
git checkout -b feature/new-feature

# 2. 开发和提交
git add .
git commit -m "feat: add new feature"

# 3. 推送分支
git push origin feature/new-feature

# 4. 创建Pull Request
# 5. 代码审查
# 6. 合并到主分支
```

### 代码审查清单
- [ ] 代码符合编码规范
- [ ] 包含必要的单元测试
- [ ] 通过所有测试用例
- [ ] 性能影响评估
- [ ] 安全性检查
- [ ] 文档更新

## 📞 获取帮助

- 💻 **开发问题**: 联系开发团队
- 🧪 **测试问题**: 查看测试文档
- 🐛 **调试支持**: 联系技术专家
- 📝 **规范问题**: 参考编码规范

---

📝 **注意**: 本目录文档正在完善中，欢迎贡献开发相关文档 