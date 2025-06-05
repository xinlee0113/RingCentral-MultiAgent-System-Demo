plugins {
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.spring") version "1.9.22" apply false
    kotlin("plugin.jpa") version "1.9.22" apply false
    id("org.sonarqube") version "4.4.1.3373"
    id("jacoco")
}

allprojects {
    group = "com.ringcentral"
    version = "1.0.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")
    
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.cloud:spring-cloud-starter-config")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
        
        // 日志和监控
        implementation("net.logstash.logback:logstash-logback-encoder:7.4")
        implementation("io.micrometer:micrometer-registry-prometheus")
        
        // 测试依赖
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:kafka")
    }
    
    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
            mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }
    
    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

// 代码质量检查
sonarqube {
    properties {
        property("sonar.projectKey", "ringcentral-multiagent-system")
        property("sonar.organization", "ringcentral")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

// 自定义任务
tasks.register("buildAll") {
    group = "build"
    description = "构建所有模块"
    dependsOn(subprojects.map { "${it.path}:build" })
}

tasks.register("testAll") {
    group = "verification"
    description = "运行所有测试"
    dependsOn(subprojects.map { "${it.path}:test" })
}

tasks.register("dockerBuildAll") {
    group = "docker"
    description = "构建所有Docker镜像"
    dependsOn(
        ":platform-services:api-gateway:dockerBuild",
        ":platform-services:auth-service:dockerBuild",
        ":agent-services:meeting-agent:dockerBuild",
        ":agent-services:call-agent:dockerBuild",
        ":ai-engines:speech-engine:dockerBuild",
        ":ai-engines:nlu-engine:dockerBuild"
    )
}