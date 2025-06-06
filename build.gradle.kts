plugins {
    id("jacoco")
    id("java-conventions")
}

allprojects {
    group = "com.ringcentral"
    version = property("version.project").toString()

    // 全局依赖解析策略
    configurations.all {
        // 排除所有snakeyaml传递依赖
        exclude(group = "org.yaml", module = "snakeyaml")

        resolutionStrategy {
            // 强制使用特定版本，避免Android变体问题
            force("org.yaml:snakeyaml:${property("version.snakeyaml")}")

            // 依赖替换策略
            dependencySubstitution {
                // 如果遇到Android变体，替换为标准版本
                substitute(module("org.yaml:snakeyaml")).using(module("org.yaml:snakeyaml:${property("version.snakeyaml")}"))
            }

            // 缓存动态版本
            cacheDynamicVersionsFor(10, "minutes")
            cacheChangingModulesFor(4, "hours")

            // 依赖解析失败时的重试策略
            eachDependency {
                if (requested.group == "org.yaml" && requested.name == "snakeyaml") {
                    useVersion(property("version.snakeyaml").toString())
                    because("统一snakeyaml版本，避免Android变体问题")
                }
            }

            // 排除Android变体
            componentSelection {
                all {
                    if (candidate.group == "org.yaml" && candidate.module == "snakeyaml") {
                        if (candidate.version.contains("android")) {
                            reject("排除Android变体")
                        }
                    }
                }
            }

            // 强制使用 gradle.properties 中定义的版本
            force(
                // Spring Boot Starters - 添加缺失的版本
                "org.springframework.boot:spring-boot-starter-web:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-data-jpa:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-data-redis:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-security:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-actuator:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-validation:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-websocket:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-test:${property("version.springBoot")}",
                "org.springframework.boot:spring-boot-starter-webflux:${property("version.springBoot")}",

                // Spring Cloud - 添加版本
                "org.springframework.cloud:spring-cloud-starter-gateway:${property("version.springCloud")}",
                "org.springframework.cloud:spring-cloud-starter-config:${property("version.springCloud")}",
                "org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:${property("version.springCloud")}",
                "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:${property("version.springCloud")}",
                "org.springframework.cloud:spring-cloud-starter-loadbalancer:${property("version.springCloud")}",

                // Spring Security OAuth2
                "org.springframework.security:spring-security-oauth2-client:${property("version.springSecurity")}",
                "org.springframework.security:spring-security-oauth2-jose:${property("version.springSecurity")}",
                "org.springframework.security:spring-security-oauth2-authorization-server:${property("version.springAuthorizationServer")}",
                "org.springframework.security:spring-security-oauth2-resource-server:${property("version.springSecurity")}",
                "org.springframework.security:spring-security-test:${property("version.springSecurity")}",

                // 数据库
                "org.postgresql:postgresql:${property("version.postgresql")}",
                "com.h2database:h2:${property("version.h2")}",
                "io.lettuce:lettuce-core:${property("version.lettuce")}",

                // AI/ML - 注释掉大型依赖，按需启用
                "dev.langchain4j:langchain4j:${property("version.langchain4j")}",
                "dev.langchain4j:langchain4j-open-ai:${property("version.langchain4j")}",
                "com.theokanning.openai-gpt3-java:service:${property("version.openaiJava")}",
                // "edu.stanford.nlp:stanford-corenlp:${property("version.stanfordNlp")}",  // 200MB+ 大型NLP库
                // "org.deeplearning4j:deeplearning4j-core:${property("version.deeplearning4j")}",  // 500MB+ 深度学习库
                // "org.nd4j:nd4j-native-platform:${property("version.nd4j")}",  // 500MB+ 数值计算库
                "org.drools:drools-core:${property("version.drools")}",
                "org.drools:drools-compiler:${property("version.drools")}",
                "org.drools:drools-mvel:${property("version.drools")}",
                "org.flowable:flowable-engine:${property("version.flowable")}",
                "org.flowable:flowable-spring-boot-starter:${property("version.flowable")}",
                // "ai.djl:api:${property("version.djl")}",  // 大型AI库
                // "ai.djl.huggingface:tokenizers:${property("version.djl")}",  // 大型AI库
                // "org.apache.spark:spark-core_2.13:${property("version.spark")}",  // 500MB+ 大数据处理库
                // "org.apache.spark:spark-sql_2.13:${property("version.spark")}",  // 500MB+ 大数据处理库
                // "org.apache.spark:spark-mllib_2.13:${property("version.spark")}",  // 500MB+ 机器学习库
                // "smile:smile-core:${property("version.smile")}",  // 机器学习库

                // 网络
                "io.netty:netty-all:${property("version.netty")}",
                "io.netty:netty-transport-native-epoll:${property("version.netty")}",
                "org.springframework.kafka:spring-kafka:${property("version.springKafka")}",
                "org.apache.kafka:kafka-streams:${property("version.kafkaStreams")}",
                "io.grpc:grpc-netty-shaded:${property("version.grpc")}",
                "io.grpc:grpc-protobuf:${property("version.grpc")}",
                "io.grpc:grpc-stub:${property("version.grpc")}",

                // JSON处理
                "com.fasterxml.jackson.core:jackson-core:${property("version.jackson")}",
                "com.fasterxml.jackson.core:jackson-databind:${property("version.jackson")}",
                "com.google.code.gson:gson:${property("version.gson")}",
                "com.google.protobuf:protobuf-java:${property("version.protobuf")}",

                // 文档处理
                "org.apache.poi:poi:${property("version.apachePoi")}",
                "org.apache.poi:poi-ooxml:${property("version.apachePoi")}",
                "com.itextpdf:itext7-core:${property("version.itext")}",
                "org.apache.tika:tika-core:${property("version.apacheTika")}",
                "org.apache.tika:tika-parsers-standard-package:${property("version.apacheTika")}",
                "org.apache.pdfbox:pdfbox:${property("version.apachePdfbox")}",
                "org.eclipse.jgit:org.eclipse.jgit:${property("version.jgit")}",

                // 媒体处理 - 注释掉大型依赖
                // "org.bytedeco:javacv-platform:${property("version.javacv")}",  // 1GB+ 视频处理库
                // "org.bytedeco:ffmpeg-platform:${property("version.ffmpeg")}",  // 1GB+ 多媒体库

                // 报表和分析
                "net.sf.jasperreports:jasperreports:${property("version.jasperreports")}",
                "com.github.signaflo:timeseries:${property("version.timeseries")}",

                // 监控
                "io.micrometer:micrometer-core:${property("version.micrometer")}",
                "io.micrometer:micrometer-registry-prometheus:${property("version.prometheus")}",
                "io.micrometer:micrometer-tracing-bridge-brave:${property("version.micrometerTracing")}",
                "io.zipkin.brave:brave:${property("version.zipkin")}",
                "io.zipkin.reporter2:zipkin-reporter-brave:${property("version.zipkinReporter")}",
                "net.logstash.logback:logstash-logback-encoder:${property("version.logstashEncoder")}",

                // 工具库
                "org.projectlombok:lombok:${property("version.lombok")}",
                "org.mapstruct:mapstruct:${property("version.mapstruct")}",
                "org.apache.commons:commons-lang3:${property("version.apacheCommons")}",
                "com.google.guava:guava:${property("version.guava")}",
                "org.slf4j:slf4j-api:${property("version.slf4j")}",
                "ch.qos.logback:logback-classic:${property("version.logback")}",
                "org.yaml:snakeyaml:${property("version.snakeyaml")}",

                // 测试
                "org.junit.jupiter:junit-jupiter:${property("version.junit")}",
                "org.mockito:mockito-core:${property("version.mockito")}",
                "org.testcontainers:junit-jupiter:${property("version.testcontainers")}",
                "org.testcontainers:postgresql:${property("version.testcontainers")}",
                "org.testcontainers:kafka:${property("version.testcontainers")}",
                "org.testcontainers:elasticsearch:${property("version.testcontainers")}",
                "com.redis:testcontainers-redis:${property("version.testcontainersRedis")}",
                "com.github.tomakehurst:wiremock-jre8:${property("version.wiremock")}",
                "io.rest-assured:rest-assured:${property("version.restAssured")}",
                "io.rest-assured:json-path:${property("version.restAssured")}",
                "io.rest-assured:xml-path:${property("version.restAssured")}",
                // "org.seleniumhq.selenium:selenium-java:${property("version.selenium")}",  // 浏览器自动化库
                // "io.github.bonigarcia:webdrivermanager:${property("version.webdrivermanager")}",  // WebDriver管理库
                "org.awaitility:awaitility:${property("version.awaitility")}",
                "org.assertj:assertj-core:${property("version.assertj")}",
                "org.hamcrest:hamcrest:${property("version.hamcrest")}",
                // "com.github.javafaker:javafaker:${property("version.javafaker")}",  // 测试数据生成库
                "org.openjdk.jmh:jmh-core:${property("version.jmh")}",
                "org.openjdk.jmh:jmh-generator-annprocess:${property("version.jmh")}",
                "com.squareup.okhttp3:mockwebserver:${property("version.okhttp")}",

                // RingCentral SDK
                "com.ringcentral:ringcentral:${property("version.ringcentral")}",
                "com.ringcentral:ringcentral-video:${property("version.ringcentralVideo")}",
                "com.ringcentral:ringcentral-voice:${property("version.ringcentralVoice")}",

                // Spring Boot Admin
                "de.codecentric:spring-boot-admin-starter-server:${property("version.springBootAdmin")}",
                "de.codecentric:spring-boot-admin-server-ui:${property("version.springBootAdmin")}",
            )
        }
    }

    // 在所有项目中显式添加snakeyaml依赖
    afterEvaluate {
        dependencies {
            if (configurations.findByName("implementation") != null) {
                add("implementation", "org.yaml:snakeyaml:${property("version.snakeyaml")}")
            }
            if (configurations.findByName("testImplementation") != null) {
                add("testImplementation", "org.yaml:snakeyaml:${property("version.snakeyaml")}")
            }
        }
    }
}

// 自定义任务
tasks.register("buildAll") {
    group = "build"
    description = "构建所有模块"
    dependsOn(subprojects.map { "${it.path}:build" })
}

tasks.register("testAll") {
    group = "verification"
    description = "运行所有测试"
    dependsOn(subprojects.map { "${it.path}:test" })
}

tasks.register("dockerBuildAll") {
    group = "docker"
    description = "构建所有Docker镜像"
    dependsOn(
        ":platform-services:api-gateway:jib",
        ":platform-services:auth-service:jib",
        ":agent-services:meeting-agent:jib",
        ":agent-services:call-agent:jib",
        ":ai-engines:speech-engine:jib",
        ":ai-engines:nlu-engine:jib",
    )
}

// 构建性能监控任务
tasks.register("buildProfile") {
    group = "build"
    description = "构建性能分析"
    doLast {
        println("请手动执行: ./gradlew build --profile --scan")
    }
}

tasks.register("cleanAll") {
    group = "build"
    description = "清理所有模块"
    dependsOn(subprojects.map { "${it.path}:clean" })
}

tasks.register("checkDependencies") {
    group = "verification"
    description = "检查依赖冲突"
    doLast {
        subprojects.forEach { project ->
            println("检查项目: ${project.name}")
            project.configurations.forEach { config ->
                if (config.isCanBeResolved) {
                    try {
                        config.resolvedConfiguration.lenientConfiguration.unresolvedModuleDependencies.forEach { dep ->
                            println("  未解析依赖: ${dep.selector}")
                        }
                    } catch (e: Exception) {
                        // 忽略无法解析的配置
                    }
                }
            }
        }
    }
}

// ============================================
// 依赖缓存管理任务
// ============================================

tasks.register("checkDependencyCache") {
    group = "dependency management"
    description = "检查Gradle依赖缓存状态"

    doLast {
        val gradleHome = gradle.gradleUserHomeDir
        val cacheDir = File(gradleHome, "caches")
        val cacheSizeMB = if (cacheDir.exists()) {
            (cacheDir.walkTopDown().filter { it.isFile() }.map { it.length() }.sum() / 1024.0 / 1024.0)
        } else {
            0.0
        }

        println("=".repeat(50))
        println("📦 Gradle依赖缓存信息")
        println("=".repeat(50))
        println("缓存目录: ${cacheDir.absolutePath}")
        println("缓存大小: ${"%.2f".format(cacheSizeMB)} MB")
        println("缓存存在: ${cacheDir.exists()}")
        println("=".repeat(50))

        if (cacheDir.exists()) {
            println("✅ 依赖缓存正常，gradle clean不会删除这些文件")
            println("💡 缓存位置: ${cacheDir.absolutePath}")
        } else {
            println("⚠️  依赖缓存目录不存在")
        }
    }
}

// 安全清理 - 只清理build目录，保护依赖缓存
tasks.register("safeclean") {
    group = "build"
    description = "安全清理：只删除build目录，保护依赖缓存"

    doLast {
        println("🧹 开始安全清理...")

        // 只清理各模块的build目录
        subprojects.forEach { project ->
            val buildDir = project.layout.buildDirectory.asFile.get()
            if (buildDir.exists()) {
                println("清理: ${buildDir.absolutePath}")
                project.delete(buildDir)
            }
        }

        // 清理根目录build
        val rootBuildDir = layout.buildDirectory.asFile.get()
        if (rootBuildDir.exists()) {
            println("清理: ${rootBuildDir.absolutePath}")
            delete(rootBuildDir)
        }

        println("✅ 安全清理完成，依赖缓存已保护")
        println("💡 提示：使用 './gradlew safeclean' 替代 './gradlew clean'")
    }
}

// 显示依赖下载信息
tasks.register("showDependencyDownload") {
    group = "dependency management"
    description = "显示依赖下载信息"

    doLast {
        println("📥 当前项目依赖概览：")
        println("- Spring Boot: ${property("version.springBoot")}")
        println("- Spring Cloud: ${property("version.springCloud")}")
        println("- Kotlin: ${property("version.kotlin")}")
        println("- Java: ${property("version.java")}")
        println("")
        println("💡 优化建议：")
        println("1. 使用 './gradlew safeclean' 替代 clean")
        println("2. 依赖缓存位置: ~/.gradle/caches/")
        println("3. 首次下载后，后续构建会使用缓存")
        println("4. 已移除大型AI库(Spark、DeepLearning4J等)，节省2GB+空间")
    }
}

// 帮助任务
tasks.register("buildHelp") {
    group = "help"
    description = "显示构建帮助信息"

    doLast {
        println(
            """
        🚀 RingCentral多智能体系统 - 构建帮助
        ============================================
        
        📦 依赖管理：
        • checkDependencyCache    - 检查依赖缓存状态
        • safeclean              - 安全清理(保护依赖缓存)
        • showDependencyDownload - 显示依赖信息
        
        🔨 构建命令：
        • ./gradlew build        - 完整构建
        • ./gradlew safeclean    - 安全清理
        • ./gradlew test         - 运行测试
        • ./gradlew buildAll     - 构建所有模块
        
        💡 重要提示：
        • 使用 safeclean 替代 clean 来保护依赖缓存
        • 依赖缓存存储在 ~/.gradle/caches/ 
        • 首次下载后会自动使用本地缓存
        • 已移除大型AI库，减少下载时间
        
        ============================================
            """.trimIndent(),
        )
    }
}

// 子项目配置
subprojects {
    apply(plugin = "java-conventions")
}
