plugins {
    `java-library`
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("jacoco")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // 项目依赖
    api(project(":shared"))

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter")
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis)
    api(libs.spring.boot.starter.security)
    api(libs.spring.cloud.starter.config)

    // 数据库
    api("org.postgresql:postgresql:${property("version.postgresql")}")
    api("io.lettuce:lettuce-core:${property("version.lettuce")}")
    api("com.zaxxer:HikariCP:5.1.0")

    // 消息队列
    api("org.springframework.kafka:spring-kafka:${property("version.springKafka")}")
    api("org.apache.kafka:kafka-clients:${property("version.kafka")}")
    api("org.apache.kafka:kafka-streams:${property("version.kafkaStreams")}")

    // AI/ML客户端
    api("dev.langchain4j:langchain4j:${property("version.langchain4j")}")
    api("dev.langchain4j:langchain4j-open-ai:${property("version.langchain4j")}")
    api("io.qdrant:client:1.7.0")
    api("com.theokanning.openai-gpt3-java:service:${property("version.openaiJava")}")

    // 网络框架
    api("io.netty:netty-all:${property("version.netty")}")
    api("com.squareup.okhttp3:okhttp:${property("version.okhttp")}")

    // 安全
    implementation(libs.spring.security.oauth2.client)
    implementation(libs.spring.security.oauth2.jose)

    // 测试依赖
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${property("version.springBoot")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("version.springCloud")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("version.testcontainers")}")
    }
} 
