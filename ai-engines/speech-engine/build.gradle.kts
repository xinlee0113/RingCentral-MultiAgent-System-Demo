plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterWebsocket)
    
    // Netty (高性能网络处理)
    implementation(Dependencies.nettyAll)
    implementation(Dependencies.nettyTransportNativeEpoll)
    
    // 语音处理
    implementation("org.bytedeco:javacv-platform:1.5.9")
    implementation("org.bytedeco:ffmpeg-platform:6.0-1.5.9")
    
    // AI语音服务集成
    implementation(Dependencies.openaiJava)
    implementation("com.azure:azure-cognitiveservices-speech:1.34.0")
    
    // 消息队列 (实时语音流)
    implementation(Dependencies.springKafka)
    implementation(Dependencies.kafkaStreams)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersKafka)
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