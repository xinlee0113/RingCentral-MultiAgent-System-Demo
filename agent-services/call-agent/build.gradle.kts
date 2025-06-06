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
    implementation(libs.spring.boot.starter.websocket)

    // RingCentral SDK - 暂时注释，依赖不存在
    // implementation(libs.ringcentral)
    // implementation(libs.ringcentral.voice)

    // AI引擎客户端
    implementation(libs.langchain4j.core)
    implementation(libs.openai.java)

    // 语音处理
    implementation(libs.netty.all)
    // // implementation(libs.javacv.platform)  // 1GB+ 视频处理库，暂时注释  // 1GB+ 视频处理库，暂时注释

    // 实时通信
    implementation(libs.spring.kafka)
    implementation(libs.kafka.streams)

    // 数据库 (通话记录)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.redis)

    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.call.CallAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ghcr.io/xinlee0113/ringcentral-multiagent-system/call-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.call.CallAgentApplication"
        ports = listOf("8080", "8081", "9090")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker",
        )
    }
} 
