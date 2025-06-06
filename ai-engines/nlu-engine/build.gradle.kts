plugins {
    id("spring-conventions")
    application
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(libs.spring.boot.starter.web)
    
    // LangChain4j集成
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.openai)
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${libs.versions.langchain4j.get()}")
    implementation("dev.langchain4j:langchain4j-hugging-face:${libs.versions.langchain4j.get()}")
    
    // NLP处理 - 使用版本目录
    implementation(libs.stanford.nlp.core)
    implementation(libs.stanford.nlp.models) {
        artifact {
            classifier = "models"
        }
    }
    
    // 机器学习 - 使用版本目录
    implementation(libs.deeplearning4j.core)
    implementation(libs.nd4j.native)
    
    // 缓存 (模型缓存)
    implementation(libs.spring.boot.starter.data.redis)
    
    // 测试依赖
    testImplementation(libs.wiremock)
}

// 解决重复依赖问题
configurations.all {
    resolutionStrategy {
        // 强制使用统一版本
        force("org.glassfish.jaxb:jaxb-core:4.0.4")
        force("org.glassfish.jaxb:jaxb-runtime:4.0.4")
        
        // 排除冲突的传递依赖
        eachDependency {
            if (requested.group == "org.glassfish.jaxb" && requested.name == "jaxb-core") {
                useVersion("4.0.4")
                because("解决重复依赖问题")
            }
        }
    }
}

// 设置重复文件处理策略
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 设置所有Copy任务的重复文件处理策略
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 设置distribution任务的重复文件处理策略
tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 应用配置
application {
    mainClass.set("com.ringcentral.ai.nlu.NluEngineApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/nlu-engine:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.ai.nlu.NluEngineApplication"
        ports = listOf("8080", "8081")
        jvmFlags = listOf(
            "-Xms1g",
            "-Xmx4g",
            "-XX:+UseG1GC"
        )
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker"
        )
    }
} 