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
    
    // RingCentral SDK
    implementation("com.ringcentral:ringcentral:3.0.0")
    implementation("com.ringcentral:ringcentral-voice:1.0.0")
    
    // AI引擎客户端
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.openaiJava)
    
    // 语音处理
    implementation(Dependencies.nettyAll)
    implementation("org.bytedeco:javacv-platform:1.5.9")
    
    // 实时通信
    implementation(Dependencies.springKafka)
    implementation(Dependencies.kafkaStreams)
    
    // 数据库 (通话记录)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.call.CallAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/call-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.call.CallAgentApplication"
        ports = listOf("8080", "8081", "9090")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 