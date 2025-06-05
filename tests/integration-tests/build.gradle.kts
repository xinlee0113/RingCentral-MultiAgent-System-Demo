plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

dependencies {
    // 项目依赖
    testImplementation(project(":shared"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":platform-services:api-gateway"))
    testImplementation(project(":platform-services:auth-service"))
    testImplementation(project(":ai-engines:speech-engine"))
    testImplementation(project(":ai-engines:nlu-engine"))
    testImplementation(project(":agent-services:meeting-agent"))
    testImplementation(project(":agent-services:call-agent"))
    
    // Spring Boot测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    
    // Testcontainers
    testImplementation(Dependencies.testcontainersJunit)
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
    testImplementation("org.testcontainers:elasticsearch:1.19.3")
    testImplementation("org.testcontainers:redis:1.19.3")
    
    // 网络测试
    testImplementation(Dependencies.wiremock)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    
    // 测试工具
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    
    // 集成测试需要更多时间
    timeout.set(Duration.ofMinutes(10))
    
    // 系统属性
    systemProperty("spring.profiles.active", "integration-test")
    
    // 测试报告
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
} 