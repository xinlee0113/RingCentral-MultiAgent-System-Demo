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
    implementation("dev.langchain4j:langchain4j-azure-open-ai:${property("version.langchain4j")}")
    implementation("dev.langchain4j:langchain4j-hugging-face:${property("version.langchain4j")}")

    // NLP处理 - NLU引擎核心依赖，需要保留
    implementation("edu.stanford.nlp:stanford-corenlp:${property("version.stanfordNlp")}") // Stanford CoreNLP - 自然语言理解必需
    // 暂时注释大型模型文件(452MB+)，按需启用
    // implementation("edu.stanford.nlp:stanford-corenlp:${property("version.stanfordNlp")}:models") { // 预训练模型
    //     artifact {
    //         classifier = "models"
    //     }
    // }

    // 机器学习 - 大型库暂时注释 (500MB+)
    // implementation(libs.deeplearning4j.core)
    // implementation(libs.nd4j.native)

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
        image = "ghcr.io/xinlee0113/ringcentral-multiagent-system-demo/nlu-engine:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.ai.nlu.NluEngineApplication"
        ports = listOf("8080", "8081")
        jvmFlags = listOf(
            "-Xms1g",
            "-Xmx4g",
            "-XX:+UseG1GC",
        )
        environment = mapOf(
            "SPRING_PROFILES_ACTIVE" to "docker",
        )
    }
} 
