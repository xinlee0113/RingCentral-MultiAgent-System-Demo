plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    
    // qDrant向量数据库
    implementation(Dependencies.qdrantClient)
    implementation("io.grpc:grpc-netty-shaded:1.59.0")
    implementation("io.grpc:grpc-protobuf:1.59.0")
    implementation("io.grpc:grpc-stub:1.59.0")
    
    // LangChain4j (向量嵌入)
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.langchain4jOpenai)
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:${Versions.langchain4j}")
    
    // LlamaIndex集成
    implementation("ai.djl:api:0.25.0")
    implementation("ai.djl.huggingface:tokenizers:0.25.0")
    
    // 文档处理
    implementation("org.apache.tika:tika-core:2.9.1")
    implementation("org.apache.tika:tika-parsers-standard-package:2.9.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")
    
    // 数据库 (元数据存储)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.ai.knowledge.KnowledgeEngineApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/knowledge-engine:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.ai.knowledge.KnowledgeEngineApplication"
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