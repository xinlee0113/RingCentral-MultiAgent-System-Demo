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
    testImplementation(Dependencies.junitJupiter)
    testImplementation(Dependencies.mockitoCore)
    testImplementation(Dependencies.mockitoJunit)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    
    // 断言库
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    
    // 测试工具
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.1")
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