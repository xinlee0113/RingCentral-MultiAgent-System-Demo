rootProject.name = "ringcentral-multiagent-system"

// 包含所有子模块
include(
    // 共享模块
    ":shared",
    ":infrastructure",

    // 平台服务
    ":platform-services:api-gateway",
    ":platform-services:auth-service",
    ":platform-services:config-service",
    ":platform-services:monitor-service",

    // AI引擎
    ":ai-engines:speech-engine",
    ":ai-engines:nlu-engine",
    ":ai-engines:knowledge-engine",
    ":ai-engines:reasoning-engine",

    // 智能体服务
    ":agent-services:meeting-agent",
    ":agent-services:call-agent",
    ":agent-services:router-agent",
    ":agent-services:analytics-agent",

    // 测试模块
    ":tests:unit-tests",
    ":tests:integration-tests",
    ":tests:e2e-tests",
    ":tests:performance-tests",
)

// 插件管理
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// 依赖解析策略
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 优先使用官方Maven Central，确保依赖完整性
        mavenCentral()
        gradlePluginPortal()

        // Spring官方仓库
        maven {
            url = uri("https://repo.spring.io/milestone")
            name = "Spring Milestone"
        }
        maven {
            url = uri("https://repo.spring.io/snapshot")
            name = "Spring Snapshot"
        }

        // Confluent仓库（Kafka相关）
        maven {
            url = uri("https://packages.confluent.io/maven/")
            name = "Confluent"
        }

        // 阿里云镜像作为备用（放在最后）
        maven {
            url = uri("https://maven.aliyun.com/repository/central")
            name = "Aliyun Central"
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/spring")
            name = "Aliyun Spring"
        }
    }
}
