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
    api(libs.postgresql)
    api("io.lettuce:lettuce-core:6.3.1.RELEASE")
    api("com.zaxxer:HikariCP:5.1.0")
    
    // 消息队列
    api(libs.spring.kafka)
    api("org.apache.kafka:kafka-clients:3.6.1")
    api("org.apache.kafka:kafka-streams:3.6.1")
    
    // AI/ML客户端
    api(libs.langchain4j.core)
    api(libs.langchain4j.openai)
    api("io.qdrant:client:1.7.0")
    api(libs.openai.java)
    
    // 网络框架
    api(libs.netty.all)
    api("com.squareup.okhttp3:okhttp:4.12.0")
    
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
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
} 