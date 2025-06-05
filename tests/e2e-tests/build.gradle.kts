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
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:json-path:5.4.0")
    testImplementation("io.rest-assured:xml-path:5.4.0")
    
    // Selenium WebDriver
    testImplementation("org.seleniumhq.selenium:selenium-java:4.16.1")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.2")
    
    // Testcontainers (完整环境)
    testImplementation(Dependencies.testcontainersJunit)
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
    testImplementation("org.testcontainers:elasticsearch:1.19.3")
    testImplementation("org.testcontainers:redis:1.19.3")
    testImplementation("org.testcontainers:nginx:1.19.3")
    
    // 测试工具
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.1")
    
    // 测试数据生成
    testImplementation("com.github.javafaker:javafaker:1.0.2")
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