plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

dependencies {
    // 项目依赖
    api(project(":shared"))
    
    // Spring Boot
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.cloud:spring-cloud-starter-config")
    
    // 数据库
    api(Dependencies.postgresql)
    api(Dependencies.redisLettuce)
    api(Dependencies.hikariCP)
    
    // 消息队列
    api(Dependencies.springKafka)
    api(Dependencies.kafkaClients)
    api(Dependencies.kafkaStreams)
    
    // AI/ML客户端
    api(Dependencies.langchain4j)
    api(Dependencies.langchain4jOpenai)
    api(Dependencies.qdrantClient)
    api(Dependencies.openaiJava)
    
    // 网络框架
    api(Dependencies.nettyAll)
    api(Dependencies.okhttp)
    
    // 安全
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    
    // 测试依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
} 