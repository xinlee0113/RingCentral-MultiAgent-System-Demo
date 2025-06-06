import java.time.Duration

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
    testImplementation(libs.spring.security.test)
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")

    // Testcontainers - 缺失依赖暂时注释
    // testImplementation(libs.bundles.testcontainers.all)
    // testImplementation(libs.testcontainers.elasticsearch)

    // 网络测试
    testImplementation(libs.wiremock)
    // testImplementation(libs.okhttp.mockwebserver)  // 缺失依赖暂时注释

    // 测试工具
    testImplementation(libs.awaitility)
    testImplementation(libs.rest.assured)
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
