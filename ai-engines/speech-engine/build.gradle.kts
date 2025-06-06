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
    
    // Netty (高性能网络处理)
    implementation(libs.netty.all)
    implementation(libs.netty.transport.native.epoll)
    
    // 语音处理
    implementation(libs.javacv.platform)
    implementation(libs.ffmpeg.platform)
    
    // AI语音服务集成
    implementation(libs.openai.java)
    // implementation("com.azure:azure-cognitiveservices-speech:1.34.0") // 暂时注释，需要添加Azure仓库
    
    // 消息队列 (实时语音流)
    implementation(libs.spring.kafka)
    implementation(libs.kafka.streams)
    
    // 测试依赖
    testImplementation(libs.testcontainers.kafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.ai.speech.SpeechEngineApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/speech-engine:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.ai.speech.SpeechEngineApplication"
        ports = listOf("8080", "8081", "9090")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 