plugins {
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

dependencies {
    // Spring Boot基础依赖
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-actuator")
    
    // JSON处理
    api(Dependencies.jacksonCore)
    api(Dependencies.jacksonDatabind)
    api(Dependencies.jacksonKotlin)
    
    // 工具库
    api(Dependencies.lombok)
    api(Dependencies.mapstruct)
    annotationProcessor(Dependencies.mapstructProcessor)
    
    // 测试依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
} 