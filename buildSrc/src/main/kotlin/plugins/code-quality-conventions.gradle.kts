plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

// 强制使用统一的Kotlin版本，避免重复下载不同版本的编译器
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-compiler-embeddable:${property("version.kotlin")}")
        force("org.jetbrains.kotlin:kotlin-stdlib:${property("version.kotlin")}")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:${property("version.kotlin")}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${property("version.kotlin")}")
    }
}

// 简化的 Spotless 配置
spotless {
    java {
        target("src/**/*.java")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint("0.50.0")
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
    
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.50.0")
    }
}

// 简化的 Detekt 配置
detekt {
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/baseline.xml")
    buildUponDefaultConfig = true
    allRules = false
    // 使用统一版本
    toolVersion = property("version.detekt").toString()
}

// 配置 detekt 任务
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
}

// 将代码质量检查集成到 check 任务中
tasks.named("check") {
    dependsOn("spotlessCheck", "detekt")
} 