plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.20")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.9.20")
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.0")
    implementation("org.sonarqube:org.sonarqube.gradle.plugin:4.4.1.3373")
} 