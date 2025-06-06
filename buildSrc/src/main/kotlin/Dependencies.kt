import org.gradle.api.Project

// 版本读取函数
fun Project.getVersion(key: String): String = property(key).toString()

object Dependencies {
    // Spring Boot Starters - 版本由 Spring Boot BOM 管理
    const val springBootStarterWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootStarterWebflux = "org.springframework.boot:spring-boot-starter-webflux"
    const val springBootStarterData = "org.springframework.boot:spring-boot-starter-data-jpa"
    const val springBootStarterSecurity = "org.springframework.boot:spring-boot-starter-security"
    const val springBootStarterActuator = "org.springframework.boot:spring-boot-starter-actuator"
    const val springBootStarterValidation = "org.springframework.boot:spring-boot-starter-validation"
    const val springBootStarterWebsocket = "org.springframework.boot:spring-boot-starter-websocket"
    const val springBootStarterDataRedis = "org.springframework.boot:spring-boot-starter-data-redis"

    // Spring Cloud - 版本由 Spring Cloud BOM 管理
    const val springCloudGateway = "org.springframework.cloud:spring-cloud-starter-gateway"
    const val springCloudConfig = "org.springframework.cloud:spring-cloud-starter-config"
    const val springCloudEureka = "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    const val springCloudLoadBalancer = "org.springframework.cloud:spring-cloud-starter-loadbalancer"

    // 数据库驱动 - 需要显式版本
    const val postgresql = "org.postgresql:postgresql"
    const val redisLettuce = "io.lettuce:lettuce-core"
    const val hikariCP = "com.zaxxer:HikariCP"
    const val h2 = "com.h2database:h2"

    // 消息队列 - 需要显式版本
    const val kafkaClients = "org.apache.kafka:kafka-clients"
    const val kafkaStreams = "org.apache.kafka:kafka-streams"
    const val springKafka = "org.springframework.kafka:spring-kafka"

    // AI/ML集成 - 需要显式版本
    const val langchain4j = "dev.langchain4j:langchain4j"
    const val langchain4jOpenai = "dev.langchain4j:langchain4j-open-ai"
    const val qdrantClient = "io.qdrant:client"
    const val openaiJava = "com.theokanning.openai-gpt3-java:service"
    const val stanfordNlpCore = "edu.stanford.nlp:stanford-corenlp"
    const val deeplearning4jCore = "org.deeplearning4j:deeplearning4j-core"
    const val nd4jNative = "org.nd4j:nd4j-native-platform"
    const val droolsCore = "org.drools:drools-core"
    const val droolsCompiler = "org.drools:drools-compiler"
    const val droolsMvel = "org.drools:drools-mvel"
    const val flowableEngine = "org.flowable:flowable-engine"
    const val djlApi = "ai.djl:api"
    const val sparkCore = "org.apache.spark:spark-core_2.13"
    const val sparkSql = "org.apache.spark:spark-sql_2.13"
    const val sparkMllib = "org.apache.spark:spark-mllib_2.13"
    const val smileCore = "smile:smile-core"

    // 网络框架 - 需要显式版本
    const val nettyAll = "io.netty:netty-all"
    const val nettyTransportNativeEpoll = "io.netty:netty-transport-native-epoll"
    const val okhttp = "com.squareup.okhttp3:okhttp"
    const val grpcNettyShaded = "io.grpc:grpc-netty-shaded"
    const val grpcProtobuf = "io.grpc:grpc-protobuf"
    const val grpcStub = "io.grpc:grpc-stub"

    // JSON处理 - 需要显式版本
    const val jacksonCore = "com.fasterxml.jackson.core:jackson-core"
    const val jacksonDatabind = "com.fasterxml.jackson.core:jackson-databind"
    const val jacksonKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
    const val gson = "com.google.code.gson:gson"
    const val protobufJava = "com.google.protobuf:protobuf-java"

    // 文档处理 - 需要显式版本
    const val apachePoi = "org.apache.poi:poi"
    const val apachePoiOoxml = "org.apache.poi:poi-ooxml"
    const val itextCore = "com.itextpdf:itext7-core"
    const val apacheTikaCore = "org.apache.tika:tika-core"
    const val apacheTikaParsers = "org.apache.tika:tika-parsers-standard-package"
    const val apachePdfbox = "org.apache.pdfbox:pdfbox"
    const val jgit = "org.eclipse.jgit:org.eclipse.jgit"

    // 媒体处理 - 需要显式版本
    const val javacvPlatform = "org.bytedeco:javacv-platform"
    const val ffmpegPlatform = "org.bytedeco:ffmpeg-platform"

    // 报表和分析 - 需要显式版本
    const val jasperreports = "net.sf.jasperreports:jasperreports"
    const val timeseries = "com.github.signaflo:timeseries"

    // 监控和日志 - 需要显式版本
    const val micrometerCore = "io.micrometer:micrometer-core"
    const val micrometerPrometheus = "io.micrometer:micrometer-registry-prometheus"
    const val micrometerTracingBrave = "io.micrometer:micrometer-tracing-bridge-brave"
    const val zipkinBrave = "io.zipkin.brave:brave"
    const val zipkinReporter = "io.zipkin.reporter2:zipkin-reporter-brave"
    const val logstashEncoder = "net.logstash.logback:logstash-logback-encoder"
    const val slf4jApi = "org.slf4j:slf4j-api"
    const val logbackClassic = "ch.qos.logback:logback-classic"

    // 工具库 - 需要显式版本
    const val lombok = "org.projectlombok:lombok"
    const val mapstruct = "org.mapstruct:mapstruct"
    const val mapstructProcessor = "org.mapstruct:mapstruct-processor"
    const val apacheCommonsLang = "org.apache.commons:commons-lang3"
    const val guava = "com.google.guava:guava"
    const val snakeyaml = "org.yaml:snakeyaml"

    // 测试依赖 - 需要显式版本
    const val junitJupiter = "org.junit.jupiter:junit-jupiter"
    const val mockitoCore = "org.mockito:mockito-core"
    const val mockitoJunit = "org.mockito:mockito-junit-jupiter"
    const val testcontainersJunit = "org.testcontainers:junit-jupiter"
    const val testcontainersPostgresql = "org.testcontainers:postgresql"
    const val testcontainersKafka = "org.testcontainers:kafka"
    const val testcontainersRedis = "com.redis:testcontainers-redis"
    const val testcontainersElasticsearch = "org.testcontainers:elasticsearch"
    const val wiremock = "com.github.tomakehurst:wiremock-jre8"
    const val restAssured = "io.rest-assured:rest-assured"
    const val selenium = "org.seleniumhq.selenium:selenium-java"
    const val webdrivermanager = "io.github.bonigarcia:webdrivermanager"
    const val awaitility = "org.awaitility:awaitility"
    const val assertj = "org.assertj:assertj-core"
    const val hamcrest = "org.hamcrest:hamcrest"
    const val javafaker = "com.github.javafaker:javafaker"

    // RingCentral SDK - 需要显式版本
    const val ringcentral = "com.ringcentral:ringcentral"
    const val ringcentralVideo = "com.ringcentral:ringcentral-video"
    const val ringcentralVoice = "com.ringcentral:ringcentral-voice"

    // 版本化依赖函数 - 在构建脚本中使用
    object Versioned {
        // 数据库
        fun postgresql(project: Project) = "${Dependencies.postgresql}:${project.getVersion("version.postgresql")}"
        fun lettuce(project: Project) = "${Dependencies.redisLettuce}:${project.getVersion("version.lettuce")}"
        fun h2(project: Project) = "${Dependencies.h2}:${project.getVersion("version.h2")}"

        // AI/ML
        fun langchain4j(project: Project) = "${Dependencies.langchain4j}:${project.getVersion("version.langchain4j")}"
        fun openaiJava(project: Project) = "${Dependencies.openaiJava}:${project.getVersion("version.openaiJava")}"
        fun stanfordNlp(project: Project) = "${Dependencies.stanfordNlpCore}:${project.getVersion("version.stanfordNlp")}"
        fun deeplearning4j(project: Project) = "${Dependencies.deeplearning4jCore}:${project.getVersion("version.deeplearning4j")}"
        fun nd4j(project: Project) = "${Dependencies.nd4jNative}:${project.getVersion("version.nd4j")}"
        fun droolsCore(project: Project) = "${Dependencies.droolsCore}:${project.getVersion("version.drools")}"
        fun flowable(project: Project) = "${Dependencies.flowableEngine}:${project.getVersion("version.flowable")}"
        fun djl(project: Project) = "${Dependencies.djlApi}:${project.getVersion("version.djl")}"
        fun spark(project: Project) = "${Dependencies.sparkCore}:${project.getVersion("version.spark")}"
        fun smile(project: Project) = "${Dependencies.smileCore}:${project.getVersion("version.smile")}"

        // 网络
        fun nettyAll(project: Project) = "${Dependencies.nettyAll}:${project.getVersion("version.netty")}"
        fun grpc(project: Project) = "${Dependencies.grpcNettyShaded}:${project.getVersion("version.grpc")}"
        fun kafka(project: Project) = "${Dependencies.kafkaStreams}:${project.getVersion("version.kafkaStreams")}"
        fun springKafka(project: Project) = "${Dependencies.springKafka}:${project.getVersion("version.springKafka")}"

        // JSON
        fun jackson(project: Project) = "${Dependencies.jacksonCore}:${project.getVersion("version.jackson")}"
        fun gson(project: Project) = "${Dependencies.gson}:${project.getVersion("version.gson")}"
        fun protobuf(project: Project) = "${Dependencies.protobufJava}:${project.getVersion("version.protobuf")}"

        // 文档处理
        fun apachePoi(project: Project) = "${Dependencies.apachePoi}:${project.getVersion("version.apachePoi")}"
        fun apachePoiOoxml(project: Project) = "${Dependencies.apachePoiOoxml}:${project.getVersion("version.apachePoi")}"
        fun itextCore(project: Project) = "${Dependencies.itextCore}:${project.getVersion("version.itext")}"
        fun apacheTika(project: Project) = "${Dependencies.apacheTikaCore}:${project.getVersion("version.apacheTika")}"
        fun apacheTikaParsers(project: Project) = "${Dependencies.apacheTikaParsers}:${project.getVersion("version.apacheTika")}"
        fun apachePdfbox(project: Project) = "${Dependencies.apachePdfbox}:${project.getVersion("version.apachePdfbox")}"
        fun jgit(project: Project) = "${Dependencies.jgit}:${project.getVersion("version.jgit")}"

        // 媒体处理
        fun javacv(project: Project) = "${Dependencies.javacvPlatform}:${project.getVersion("version.javacv")}"
        fun ffmpeg(project: Project) = "${Dependencies.ffmpegPlatform}:${project.getVersion("version.ffmpeg")}"

        // 报表和分析
        fun jasperreports(project: Project) = "${Dependencies.jasperreports}:${project.getVersion("version.jasperreports")}"
        fun timeseries(project: Project) = "${Dependencies.timeseries}:${project.getVersion("version.timeseries")}"

        // 监控
        fun micrometer(project: Project) = "${Dependencies.micrometerCore}:${project.getVersion("version.micrometer")}"
        fun prometheus(project: Project) = "${Dependencies.micrometerPrometheus}:${project.getVersion("version.prometheus")}"
        fun zipkin(project: Project) = "${Dependencies.zipkinBrave}:${project.getVersion("version.zipkin")}"
        fun logstash(project: Project) = "${Dependencies.logstashEncoder}:${project.getVersion("version.logstashEncoder")}"

        // 工具库
        fun lombok(project: Project) = "${Dependencies.lombok}:${project.getVersion("version.lombok")}"
        fun mapstruct(project: Project) = "${Dependencies.mapstruct}:${project.getVersion("version.mapstruct")}"
        fun guava(project: Project) = "${Dependencies.guava}:${project.getVersion("version.guava")}"
        fun commonsLang(project: Project) = "${Dependencies.apacheCommonsLang}:${project.getVersion("version.apacheCommons")}"
        fun snakeyaml(project: Project) = "${Dependencies.snakeyaml}:${project.getVersion("version.snakeyaml")}"

        // 测试
        fun junit(project: Project) = "${Dependencies.junitJupiter}:${project.getVersion("version.junit")}"
        fun mockito(project: Project) = "${Dependencies.mockitoCore}:${project.getVersion("version.mockito")}"
        fun testcontainers(project: Project) = "${Dependencies.testcontainersJunit}:${project.getVersion("version.testcontainers")}"
        fun testcontainersRedis(project: Project) = "${Dependencies.testcontainersRedis}:${project.getVersion("version.testcontainersRedis")}"
        fun wiremock(project: Project) = "${Dependencies.wiremock}:${project.getVersion("version.wiremock")}"
        fun restAssured(project: Project) = "${Dependencies.restAssured}:${project.getVersion("version.restAssured")}"
        fun selenium(project: Project) = "${Dependencies.selenium}:${project.getVersion("version.selenium")}"
        fun awaitility(project: Project) = "${Dependencies.awaitility}:${project.getVersion("version.awaitility")}"
        fun assertj(project: Project) = "${Dependencies.assertj}:${project.getVersion("version.assertj")}"
        fun hamcrest(project: Project) = "${Dependencies.hamcrest}:${project.getVersion("version.hamcrest")}"
        fun javafaker(project: Project) = "${Dependencies.javafaker}:${project.getVersion("version.javafaker")}"

        // RingCentral
        fun ringcentral(project: Project) = "${Dependencies.ringcentral}:${project.getVersion("version.ringcentral")}"
        fun ringcentralVideo(project: Project) = "${Dependencies.ringcentralVideo}:${project.getVersion("version.ringcentralVideo")}"
        fun ringcentralVoice(project: Project) = "${Dependencies.ringcentralVoice}:${project.getVersion("version.ringcentralVoice")}"
    }
}
