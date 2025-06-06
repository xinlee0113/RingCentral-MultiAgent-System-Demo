plugins {
    id("spring-conventions")
    application
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // OAuth2 和 JWT
    implementation(libs.spring.security.oauth2.authorization.server)
    implementation(libs.spring.security.oauth2.resource.server)
    implementation(libs.spring.security.oauth2.jose)
    
    // 数据库
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // 测试依赖
    testImplementation(libs.spring.security.test)
    testImplementation("org.testcontainers:postgresql")
}

// 应用配置
application {
    mainClass.set("com.ringcentral.auth.AuthServiceApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/auth-service:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.auth.AuthServiceApplication"
        ports = listOf("8080", "8081")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 