plugins {
    id("spring-conventions")
    application
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))

    // Spring Cloud Config
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterSecurity)

    // Git支持
    implementation(libs.jgit)

    // 测试依赖
    testImplementation("org.springframework.cloud:spring-cloud-config-server")
}

// 应用配置
application {
    mainClass.set("com.ringcentral.config.ConfigServiceApplication")
}

// Docker配置
jib {
    to {
        image = "ghcr.io/xinlee0113/ringcentral-multiagent-system/config-service:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.config.ConfigServiceApplication"
        ports = listOf("8888")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker",
        )
    }
} 
