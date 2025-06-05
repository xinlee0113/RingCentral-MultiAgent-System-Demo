plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Cloud Gateway
    implementation(Dependencies.springCloudGateway)
    implementation(Dependencies.springCloudLoadBalancer)
    
    // Spring Boot WebFlux (Gateway需要)
    implementation(Dependencies.springBootStarterWebflux)
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // Redis (限流和缓存)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation(Dependencies.wiremock)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.gateway.GatewayApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/api-gateway:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.gateway.GatewayApplication"
        ports = listOf("8080", "8081")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 