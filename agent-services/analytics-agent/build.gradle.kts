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
    
    // 数据分析 - 暂时注释有问题的依赖
    // implementation(libs.bundles.analytics)
    
    // AI分析引擎
    implementation(libs.langchain4j.core)
    implementation(libs.openai.java)
    
    // 机器学习 - 暂时注释有问题的依赖
    // implementation(libs.smile.core)
    
    // 时间序列分析
    implementation(libs.timeseries)
    
    // 数据库 (分析结果存储)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.redis)
    
    // 消息队列 (实时数据流)
    implementation(libs.spring.kafka)
    implementation(libs.kafka.streams)
    
    // 报表生成 - 暂时注释有问题的依赖
    // implementation(libs.jasperreports)
    implementation(libs.bundles.document.processing)
    
    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.analytics.AnalyticsAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/analytics-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.analytics.AnalyticsAgentApplication"
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