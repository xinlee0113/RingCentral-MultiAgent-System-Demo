plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    
    // Spring Cloud Gateway (路由功能)
    implementation(Dependencies.springCloudGateway)
    implementation(Dependencies.springCloudLoadBalancer)
    
    // AI决策引擎
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.openaiJava)
    
    // 规则引擎 (路由规则)
    implementation("org.drools:drools-core:8.44.0.Final")
    implementation("org.drools:drools-compiler:8.44.0.Final")
    
    // 负载均衡和服务发现
    implementation(Dependencies.springCloudEureka)
    
    // 数据库 (路由历史和规则)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.wiremock)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.router.RouterAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/router-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.router.RouterAgentApplication"
        ports = listOf("8080", "8081")
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 