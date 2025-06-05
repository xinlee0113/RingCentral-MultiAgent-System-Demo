plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("io.gatling.gradle") version "3.9.5.6"
}

dependencies {
    // 项目依赖
    testImplementation(project(":shared"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":platform-services:api-gateway"))
    testImplementation(project(":agent-services:meeting-agent"))
    testImplementation(project(":agent-services:call-agent"))
    
    // JMH (Java Microbenchmark Harness)
    testImplementation("org.openjdk.jmh:jmh-core:1.37")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    
    // Gatling (负载测试)
    gatling("io.gatling.highcharts:gatling-charts-highcharts:3.9.5")
    gatling("io.gatling:gatling-app:3.9.5")
    gatling("io.gatling:gatling-recorder:3.9.5")
    
    // Spring Boot测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    
    // 性能监控
    testImplementation("io.micrometer:micrometer-core:1.12.0")
    testImplementation("io.micrometer:micrometer-registry-prometheus:1.12.0")
    
    // 测试工具
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    
    // Testcontainers
    testImplementation(Dependencies.testcontainersJunit)
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
}

// Gatling配置
gatling {
    toolVersion = "3.9.5"
    includeMainOutput = true
    includeTestOutput = true
    scalaVersion = "2.13.12"
    jvmArgs = listOf(
        "-server",
        "-Xmx2G",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:+UseG1GC",
        "-XX:+UseStringDeduplication",
        "-Djava.net.preferIPv4Stack=true",
        "-Djava.net.preferIPv6Addresses=false"
    )
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    
    // 性能测试需要更多资源
    maxHeapSize = "4g"
    jvmArgs = listOf(
        "-XX:+UseG1GC",
        "-XX:+UseStringDeduplication"
    )
    
    // 系统属性
    systemProperty("spring.profiles.active", "performance-test")
    
    // 测试报告
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

// JMH基准测试任务
tasks.register<JavaExec>("jmh") {
    group = "benchmark"
    description = "运行JMH基准测试"
    dependsOn(tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("org.openjdk.jmh.Main")
    args = listOf(
        "-rf", "json",
        "-rff", "build/reports/jmh/results.json"
    )
} 