import java.util.Properties

plugins {
    `kotlin-dsl`
}

// 从根项目的gradle.properties读取版本信息
val rootProperties = Properties().apply {
    load(rootProject.file("../gradle.properties").inputStream())
}

fun getVersion(key: String): String {
    return rootProperties.getProperty(key) ?: throw GradleException("Version property '$key' not found in root gradle.properties")
}

// 设置Java版本兼容性 - 从gradle.properties读取
java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${getVersion("version.java")}")
    targetCompatibility = JavaVersion.valueOf("VERSION_${getVersion("version.java")}")
}

// 配置Kotlin编译选项 - 从gradle.properties读取版本
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(getVersion("version.java")))
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
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
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${getVersion("version.springBoot")}")
    implementation("io.spring.gradle:dependency-management-plugin:${getVersion("version.dependencyManagement")}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${getVersion("version.kotlin")}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${getVersion("version.kotlin")}")
    implementation("org.jetbrains.kotlin:kotlin-noarg:${getVersion("version.kotlin")}")
    implementation("com.google.cloud.tools:jib-gradle-plugin:${getVersion("version.jib")}")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:${getVersion("version.sonarqube")}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${getVersion("version.spotless")}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${getVersion("version.detekt")}")
} 