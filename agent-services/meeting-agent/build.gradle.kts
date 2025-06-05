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
    implementation("com.ringcentral:ringcentral-video:1.0.0")
    
    // AI引擎客户端
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.openaiJava)
    
    // 实时通信
    implementation(Dependencies.nettyAll)
    implementation(Dependencies.springKafka)
    
    // 数据库 (会议记录)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 文档生成
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("com.itextpdf:itext7-core:8.0.2")
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.meeting.MeetingAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/meeting-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.meeting.MeetingAgentApplication"
        ports = listOf("8080", "8081", "9090")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
}