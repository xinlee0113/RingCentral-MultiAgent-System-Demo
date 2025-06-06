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
    // implementation(libs.ringcentral.video)

    // AI引擎客户端
    implementation(libs.langchain4j.core)
    implementation(libs.openai.java)

    // 实时通信
    implementation(libs.netty.all)
    implementation(libs.spring.kafka)

    // 数据库 (会议记录)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.redis)

    // 文档生成
    implementation(libs.bundles.document.processing)

    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.meeting.MeetingAgentApplication")
}

// Docker配置
jib {
    to {
        image = "xinlee0113/meeting-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.meeting.MeetingAgentApplication"
        ports = listOf("8080", "8081", "9090")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker",
        )
    }
}
