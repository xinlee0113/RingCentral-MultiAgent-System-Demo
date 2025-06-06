plugins {
    id("java-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    
    // 监控和日志
    implementation("net.logstash.logback:logstash-logback-encoder:${property("version.logstashEncoder")}")
    implementation("io.micrometer:micrometer-registry-prometheus:${property("version.prometheus")}")
    
    // 开发工具
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // 测试依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${property("version.springBoot")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("version.springCloud")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("version.testcontainers")}")
    }
}

// Docker镜像构建配置
jib {
    from {
        image = "openjdk:17-jre-slim"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "ringcentral/${project.name}:${project.version}"
        tags = setOf("latest", project.version.toString())
    }
    container {
        jvmFlags = listOf(
            "-Xms512m",
            "-Xmx2g",
            "-XX:+UseG1GC",
            "-XX:+UseContainerSupport",
            "-Djava.security.egd=file:/dev/./urandom"
        )
        ports = listOf("8080", "8081")
        labels = mapOf(
            "maintainer" to "RingCentral AI Team",
            "version" to project.version.toString(),
            "description" to "RingCentral MultiAgent System - ${project.name}"
        )
    }
}

// Spring Boot任务配置
tasks.bootJar {
    archiveFileName.set("${project.name}.jar")
    layered {
        enabled = true
    }
}

tasks.register("dockerBuild") {
    group = "docker"
    description = "构建Docker镜像"
    dependsOn(tasks.jib)
} 