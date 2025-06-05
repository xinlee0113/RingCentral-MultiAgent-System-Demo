plugins {
    id("spring-conventions")
}

dependencies {
    // 项目依赖
    implementation(project(":shared"))
    implementation(project(":infrastructure"))
    
    // Spring Boot Web
    implementation(Dependencies.springBootStarterWeb)
    
    // 数据分析
    implementation("org.apache.spark:spark-core_2.13:3.5.0")
    implementation("org.apache.spark:spark-sql_2.13:3.5.0")
    implementation("org.apache.spark:spark-streaming_2.13:3.5.0")
    
    // AI分析引擎
    implementation(Dependencies.langchain4j)
    implementation(Dependencies.openaiJava)
    
    // 机器学习
    implementation("org.apache.spark:spark-mllib_2.13:3.5.0")
    implementation("smile:smile-core:3.0.2")
    
    // 时间序列分析
    implementation("com.github.signaflo:timeseries:0.4")
    
    // 数据库 (分析结果存储)
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.postgresql)
    implementation(Dependencies.springBootStarterDataRedis)
    
    // 消息队列 (实时数据流)
    implementation(Dependencies.springKafka)
    implementation(Dependencies.kafkaStreams)
    
    // 报表生成
    implementation("net.sf.jasperreports:jasperreports:6.20.6")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    
    // 测试依赖
    testImplementation(Dependencies.testcontainersPostgresql)
    testImplementation(Dependencies.testcontainersKafka)
}

// 应用配置
application {
    mainClass.set("com.ringcentral.agent.analytics.AnalyticsAgentApplication")
}

// Docker配置
jib {
    to {
        image = "ringcentral/analytics-agent:${project.version}"
    }
    container {
        mainClass = "com.ringcentral.agent.analytics.AnalyticsAgentApplication"
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