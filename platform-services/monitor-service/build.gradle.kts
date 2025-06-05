plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Admin
    implementation("de.codecentric:spring-boot-admin-starter-server:3.2.0")
    implementation("de.codecentric:spring-boot-admin-server-ui:3.2.0")
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    
    // 监控和指标
    implementation(Dependencies.micrometerPrometheus)
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    
    // 数据库 (存储监控数据)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.monitor.MonitorServiceApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/monitor-service:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.monitor.MonitorServiceApplication"
        ports = listOf("8080", "8081")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 