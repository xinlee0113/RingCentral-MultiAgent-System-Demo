object Dependencies {
    // Spring Boot Starters
    const val springBootStarterWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootStarterWebflux = "org.springframework.boot:spring-boot-starter-webflux"
    const val springBootStarterData = "org.springframework.boot:spring-boot-starter-data-jpa"
    const val springBootStarterSecurity = "org.springframework.boot:spring-boot-starter-security"
    const val springBootStarterActuator = "org.springframework.boot:spring-boot-starter-actuator"
    const val springBootStarterValidation = "org.springframework.boot:spring-boot-starter-validation"
    const val springBootStarterWebsocket = "org.springframework.boot:spring-boot-starter-websocket"
    const val springBootStarterDataRedis = "org.springframework.boot:spring-boot-starter-data-redis"
    
    // Spring Cloud
    const val springCloudGateway = "org.springframework.cloud:spring-cloud-starter-gateway"
    const val springCloudConfig = "org.springframework.cloud:spring-cloud-starter-config"
    const val springCloudEureka = "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    const val springCloudLoadBalancer = "org.springframework.cloud:spring-cloud-starter-loadbalancer"
    
    // 数据库驱动
    const val postgresql = "org.postgresql:postgresql"
    const val redisLettuce = "io.lettuce:lettuce-core"
    const val hikariCP = "com.zaxxer:HikariCP"
    
    // 消息队列
    const val kafkaClients = "org.apache.kafka:kafka-clients"
    const val kafkaStreams = "org.apache.kafka:kafka-streams"
    const val springKafka = "org.springframework.kafka:spring-kafka"
    
    // AI/ML集成
    const val langchain4j = "dev.langchain4j:langchain4j"
    const val langchain4jOpenai = "dev.langchain4j:langchain4j-open-ai"
    const val qdrantClient = "io.qdrant:client"
    const val openaiJava = "com.theokanning.openai-gpt3-java:service"
    
    // 网络框架
    const val nettyAll = "io.netty:netty-all"
    const val nettyTransportNativeEpoll = "io.netty:netty-transport-native-epoll"
    const val okhttp = "com.squareup.okhttp3:okhttp"
    
    // JSON处理
    const val jacksonCore = "com.fasterxml.jackson.core:jackson-core"
    const val jacksonDatabind = "com.fasterxml.jackson.core:jackson-databind"
    const val jacksonKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
    
    // 工具库
    const val lombok = "org.projectlombok:lombok"
    const val mapstruct = "org.mapstruct:mapstruct"
    const val mapstructProcessor = "org.mapstruct:mapstruct-processor"
    
    // 测试依赖
    const val junitJupiter = "org.junit.jupiter:junit-jupiter"
    const val mockitoCore = "org.mockito:mockito-core"
    const val mockitoJunit = "org.mockito:mockito-junit-jupiter"
    const val testcontainersJunit = "org.testcontainers:junit-jupiter"
    const val testcontainersPostgresql = "org.testcontainers:postgresql"
    const val testcontainersKafka = "org.testcontainers:kafka"
    const val wiremock = "com.github.tomakehurst:wiremock-jre8"
    
    // 监控和日志
    const val micrometerPrometheus = "io.micrometer:micrometer-registry-prometheus"
    const val logstashEncoder = "net.logstash.logback:logstash-logback-encoder"
    const val slf4jApi = "org.slf4j:slf4j-api"
} 