plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    
    // LangChain4j集成
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.langchain4jOpenai)
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${Versions.langchain4j}")
    implementation("dev.langchain4j:langchain4j-hugging-face:${Versions.langchain4j}")
    
    // NLP处理
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.4")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.4:models")
    
    // 机器学习
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M2.1")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-M2.1")
    
    // 缓存 (模型缓存)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 测试依赖
    testImplementation(Dependencies.wiremock)
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