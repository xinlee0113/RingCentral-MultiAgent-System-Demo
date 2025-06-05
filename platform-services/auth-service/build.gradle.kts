plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springBootStarterData)
    
    // OAuth2 和 JWT
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    
    // 数据库
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(Dependencies.testcontainersPostgresql)
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