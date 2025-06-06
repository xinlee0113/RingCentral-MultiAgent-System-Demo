plugins {
    id("spring-conventions")
    application
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))

    // Spring Boot Admin
    implementation(libs.spring.boot.admin.server)
    implementation(libs.spring.boot.admin.ui)

    // Spring Boot Web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)

    // 监控和指标
    implementation(libs.micrometer.prometheus)
    implementation(libs.micrometer.tracing.bridge.brave)
    implementation(libs.zipkin.reporter.brave)

    // 数据库 (存储监控数据)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)

    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
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
            "SPRING_PROFILES_ACTIVE" to "docker",
        )
    }
} 
