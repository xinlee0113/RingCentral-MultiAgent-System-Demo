plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    
    // AutoGen集成 (多智能体协作)
    implementation("com.microsoft.autogen:autogen-java:0.2.0")
    implementation("com.microsoft.semantic-kernel:semantickernel-api:1.0.0")
    
    // LangChain4j (推理链)
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.langchain4jOpenai)
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${Versions.langchain4j}")
    
    // 规则引擎
    implementation("org.drools:drools-core:8.44.0.Final")
    implementation("org.drools:drools-compiler:8.44.0.Final")
    implementation("org.drools:drools-mvel:8.44.0.Final")
    
    // 工作流引擎
    implementation("org.flowable:flowable-engine:7.0.0")
    implementation("org.flowable:flowable-spring-boot-starter:7.0.0")
    
    // 数据库 (推理历史和规则存储)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.wiremock)
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