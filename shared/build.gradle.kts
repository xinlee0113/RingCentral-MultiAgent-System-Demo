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
    // Spring Boot基础依赖
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.validation)

    // JSON处理
    api(libs.jackson.core)
    api(libs.jackson.databind)
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 工具库
    api("org.projectlombok:lombok:${property("version.lombok")}")
    api("org.mapstruct:mapstruct:${property("version.mapstruct")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("version.mapstruct")}")

    // 测试依赖
    testImplementation(libs.spring.boot.starter.test)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${property("version.springBoot")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("version.springCloud")}")
    }
} 
