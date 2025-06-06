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
    
    // qDrant向量数据库
    implementation("io.qdrant:client:1.7.0") // 暂未加入版本目录
    implementation(libs.grpc.netty.shaded)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    
    // LangChain4j (向量嵌入)
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.openai)
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:${libs.versions.langchain4j.get()}")
    
    // LlamaIndex集成
    implementation(libs.djl.api)
    implementation(libs.djl.tokenizers)
    
    // 文档处理
    implementation(libs.apache.tika.core)
    implementation(libs.apache.tika.parsers)
    implementation(libs.apache.pdfbox)
    
    // 数据库 (元数据存储)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    
    // 测试依赖
    testImplementation(libs.testcontainers.postgresql)
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