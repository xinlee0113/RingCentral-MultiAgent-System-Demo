plugins {
    id("spring-conventions")
    application
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(libs.spring.boot.starter.web)
    
    // AutoGen集成 (多智能体协作) - 暂时注释，依赖不存在
    // implementation("com.microsoft.autogen:autogen-java:0.2.0")
    // implementation("com.microsoft.semantic-kernel:semantickernel-api:1.0.0")
    
    // LangChain4j (推理链)
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.openai)
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${libs.versions.langchain4j.get()}")
    
    // 规则引擎
    implementation(libs.bundles.rules.engine)
    
    // 工作流引擎
    implementation(libs.flowable.engine)
    implementation(libs.flowable.spring.boot)
    
    // 数据库 (推理历史和规则存储)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.redis)
    
    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.wiremock)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.ai.reasoning.ReasoningEngineApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/reasoning-engine:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.ai.reasoning.ReasoningEngineApplication"
        ports = listOf("8080", "8081")
        jvmFlags = listOf(
            "-Xms1g",
            "-Xmx4g",
            "-XX:+UseG1GC"
        )
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
}