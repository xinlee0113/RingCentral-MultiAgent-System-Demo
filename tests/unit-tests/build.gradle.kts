plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

dependencies {
    // 项目依赖 (测试所有模块)
    testImplementation(project(":shared"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":platform-services:api-gateway"))
    testImplementation(project(":platform-services:auth-service"))
    testImplementation(project(":ai-engines:speech-engine"))
    testImplementation(project(":ai-engines:nlu-engine"))
    testImplementation(project(":agent-services:meeting-agent"))
    testImplementation(project(":agent-services:call-agent"))

    // 测试框架
    testImplementation(libs.bundles.testing)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)

    // 扩展测试工具
    testImplementation(libs.assertj)
    testImplementation(libs.hamcrest)
    testImplementation(libs.awaitility)
    testImplementation(libs.wiremock)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }

    // 并行执行测试
    maxParallelForks = Runtime.getRuntime().availableProcessors()

    // 测试报告
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
} 
