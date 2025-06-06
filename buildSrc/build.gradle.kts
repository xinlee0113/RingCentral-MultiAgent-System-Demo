plugins {
    `kotlin-dsl`
}

repositories {
    // 使用国内镜像源提升下载速度
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/spring")
    maven("https://maven.aliyun.com/repository/spring-plugin")
    
    // 备用官方源
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.2")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.9.22")
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.0")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.4.1.3373")
} 