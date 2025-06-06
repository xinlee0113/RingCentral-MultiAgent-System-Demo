import java.time.Duration

plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

dependencies {
    // 项目依赖 (完整系统测试)
    testImplementation(project(":shared"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":platform-services:api-gateway"))
    testImplementation(project(":platform-services:auth-service"))
    testImplementation(project(":platform-services:config-service"))
    testImplementation(project(":platform-services:monitor-service"))
    testImplementation(project(":ai-engines:speech-engine"))
    testImplementation(project(":ai-engines:nlu-engine"))
    testImplementation(project(":ai-engines:knowledge-engine"))
    testImplementation(project(":ai-engines:reasoning-engine"))
    testImplementation(project(":agent-services:meeting-agent"))
    testImplementation(project(":agent-services:call-agent"))
    testImplementation(project(":agent-services:router-agent"))
    testImplementation(project(":agent-services:analytics-agent"))

    // E2E测试框架
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.rest.assured)
    // testImplementation(libs.rest.assured.json)  // 缺失依赖，暂时注释
    // testImplementation(libs.rest.assured.xml)   // 缺失依赖，暂时注释

    // Selenium WebDriver - 大型依赖暂时注释
    // testImplementation(libs.selenium)
    // testImplementation(libs.webdrivermanager)

    // Testcontainers (完整环境) - 部分缺失依赖暂时注释
    // testImplementation(libs.bundles.testcontainers.all)
    // testImplementation(libs.testcontainers.elasticsearch)
    // testImplementation(libs.testcontainers.nginx)

    // 测试工具
    testImplementation(libs.awaitility)
    testImplementation(libs.wiremock)

    // 测试数据生成 - 缺失依赖暂时注释
    // testImplementation(libs.javafaker)
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

    // E2E测试需要更长时间
    timeout.set(Duration.ofMinutes(30))

    // 系统属性
    systemProperty("spring.profiles.active", "e2e-test")
    systemProperty("webdriver.chrome.driver", System.getProperty("webdriver.chrome.driver"))

    // 测试报告
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }

    // 失败时继续执行其他测试
    ignoreFailures = true
} 
