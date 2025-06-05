plugins {
    `kotlin-dsl`
}

repositories {
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