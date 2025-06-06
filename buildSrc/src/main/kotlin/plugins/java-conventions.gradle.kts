import java.time.Duration

plugins {
    java
    jacoco
    id("org.sonarqube")
    id("code-quality-conventions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-Xlint:all",
        "-Xlint:-processing",
        "-Werror",
        "-parameters"  // 保留参数名，提升运行时性能
    ))
    
    // 编译优化
    options.isFork = true
    options.isIncremental = true
    options.forkOptions.jvmArgs?.addAll(listOf(
        "-Xmx2g",
        "-XX:+UseG1GC"
    ))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
    
    // 测试执行优化
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    forkEvery = 100  // 每100个测试重启JVM，避免内存泄漏
    
    // JVM参数优化
    jvmArgs(
        "-Xmx2g",
        "-XX:+UseG1GC",
        "-XX:+UseStringDeduplication",
        "-Djunit.jupiter.execution.parallel.enabled=true",
        "-Djunit.jupiter.execution.parallel.mode.default=concurrent"
    )
    
    // 测试超时设置
    timeout.set(Duration.ofMinutes(10))
    
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// SonarQube/SonarCloud配置
sonar {
    properties {
        // SonarCloud项目配置
        property("sonar.projectKey", "xinlee0113_RingCentral-MultiAgent-System-Demo")
        property("sonar.organization", "xinlee0113")
        property("sonar.host.url", "https://sonarcloud.io")
        
        // 项目信息
        property("sonar.projectName", "RingCentral MultiAgent System")
        property("sonar.projectDescription", "RingCentral企业级AI智能体协同平台")
        property("sonar.projectVersion", project.version.toString())
        
        // Java配置
        property("sonar.gradle.skipCompile", "true")
        property("sonar.java.source", "17")
        property("sonar.java.target", "17")
        property("sonar.java.binaries", "${layout.buildDirectory.get()}/classes")
        
        // 测试和覆盖率配置
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.junit.reportPaths", "${layout.buildDirectory.get()}/test-results/test")
        
        // 源码配置
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        
        // 排除配置
        property("sonar.exclusions", """
            **/generated/**,
            **/build/**,
            **/*.gradle.kts,
            **/gradle/**,
            **/gradlew*,
            **/.gradle/**,
            **/node_modules/**,
            **/target/**
        """.trimIndent())
        
        // 测试排除
        property("sonar.test.exclusions", """
            **/test/**/*Test.java,
            **/test/**/*Tests.java,
            **/test/**/*IT.java
        """.trimIndent())
        
        // 代码质量配置
        property("sonar.qualitygate.wait", "true")
        property("sonar.pullrequest.provider", "github")
        property("sonar.pullrequest.github.repository", "xinlee0113/RingCentral-MultiAgent-System-Demo")
    }
} 