# API和云平台技术文档

## API技术

### 1. REST API

#### 技术讲解
REST（Representational State Transfer）是一种软件架构风格，用于设计网络应用程序的API。它基于HTTP协议，使用标准的HTTP方法（GET、POST、PUT、DELETE等）来操作资源。

**主要特点：**
- 无状态：每个请求都包含处理该请求所需的所有信息
- 统一接口：使用标准HTTP方法和状态码
- 资源导向：通过URL标识资源
- 可缓存：支持HTTP缓存机制
- 分层系统：支持代理、网关等中间层

#### Demo示例

```java
// REST API控制器
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Validated
public class AIRestController {
    
    private final AIService aiService;
    private final ConversationService conversationService;
    
    public AIRestController(AIService aiService, ConversationService conversationService) {
        this.aiService = aiService;
        this.conversationService = conversationService;
    }
    
    // 创建新对话
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        
        Conversation conversation = conversationService.createConversation(
            request.getUserId(), 
            request.getTitle()
        );
        
        ConversationResponse response = ConversationResponse.builder()
            .id(conversation.getId())
            .sessionId(conversation.getSessionId())
            .title(conversation.getTitle())
            .createdAt(conversation.getCreatedAt())
            .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 获取用户对话列表
    @GetMapping("/users/{userId}/conversations")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        
        List<ConversationResponse> responses = conversations.stream()
            .skip((long) page * size)
            .limit(size)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    // 发送消息
    @PostMapping("/conversations/{sessionId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        
        // 保存用户消息
        Message userMessage = conversationService.addMessage(
            sessionId, 
            MessageRole.USER, 
            request.getContent(),
            null
        );
        
        // 生成AI回复
        String aiResponse = aiService.generateResponse(
            request.getContent(),
            request.getModel(),
            sessionId
        );
        
        // 保存AI消息
        Message aiMessage = conversationService.addMessage(
            sessionId,
            MessageRole.ASSISTANT,
            aiResponse,
            request.getModel()
        );
        
        MessageResponse response = MessageResponse.builder()
            .id(aiMessage.getId())
            .role(aiMessage.getRole())
            .content(aiMessage.getContent())
            .model(aiMessage.getModel())
            .createdAt(aiMessage.getCreatedAt())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    // 获取对话历史
    @GetMapping("/conversations/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> getConversationHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<Message> messages = conversationService.getConversationHistory(sessionId);
        
        List<MessageResponse> responses = messages.stream()
            .skip((long) page * size)
            .limit(size)
            .map(this::convertToMessageResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    // 流式聊天
    @PostMapping(value = "/conversations/{sessionId}/stream", 
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        
        SseEmitter emitter = new SseEmitter(30000L); // 30秒超时
        
        // 异步处理流式响应
        CompletableFuture.runAsync(() -> {
            try {
                // 保存用户消息
                conversationService.addMessage(
                    sessionId, 
                    MessageRole.USER, 
                    request.getContent(),
                    null
                );
                
                // 流式生成AI回复
                StringBuilder fullResponse = new StringBuilder();
                aiService.generateStreamResponse(request.getContent(), request.getModel())
                    .subscribe(
                        chunk -> {
                            try {
                                fullResponse.append(chunk);
                                emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(Map.of("content", chunk, "type", "chunk")));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        error -> emitter.completeWithError(error),
                        () -> {
                            try {
                                // 保存完整的AI回复
                                conversationService.addMessage(
                                    sessionId,
                                    MessageRole.ASSISTANT,
                                    fullResponse.toString(),
                                    request.getModel()
                                );
                                
                                emitter.send(SseEmitter.event()
                                    .name("complete")
                                    .data(Map.of("type", "complete")));
                                emitter.complete();
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                    );
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
    
    // 搜索消息
    @GetMapping("/search/messages")
    public ResponseEntity<SearchResponse> searchMessages(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String userId) {
        
        List<Message> messages = conversationService.searchMessages(query, limit);
        
        SearchResponse response = SearchResponse.builder()
            .query(query)
            .results(messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList()))
            .total(messages.size())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    // 获取可用模型
    @GetMapping("/models")
    public ResponseEntity<List<ModelInfo>> getAvailableModels() {
        List<ModelInfo> models = aiService.getAvailableModels();
        return ResponseEntity.ok(models);
    }
    
    // 健康检查
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis(),
            "version", "1.0.0"
        );
        return ResponseEntity.ok(health);
    }
    
    // 异常处理
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse error = ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(e.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse error = ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("内部服务器错误")
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    // 辅助方法
    private ConversationResponse convertToResponse(Conversation conversation) {
        return ConversationResponse.builder()
            .id(conversation.getId())
            .sessionId(conversation.getSessionId())
            .title(conversation.getTitle())
            .createdAt(conversation.getCreatedAt())
            .updatedAt(conversation.getUpdatedAt())
            .messageCount(conversation.getMessages().size())
            .build();
    }
    
    private MessageResponse convertToMessageResponse(Message message) {
        return MessageResponse.builder()
            .id(message.getId())
            .role(message.getRole())
            .content(message.getContent())
            .model(message.getModel())
            .tokenUsage(message.getTokenUsage())
            .createdAt(message.getCreatedAt())
            .build();
    }
}

// 请求和响应模型
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @Size(max = 100, message = "标题长度不能超过100字符")
    private String title;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 4000, message = "消息长度不能超过4000字符")
    private String content;
    
    @NotBlank(message = "模型不能为空")
    private String model;
    
    private Map<String, Object> parameters;
}

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private String sessionId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer messageCount;
}

@Data
@Builder
public class MessageResponse {
    private Long id;
    private MessageRole role;
    private String content;
    private String model;
    private Integer tokenUsage;
    private LocalDateTime createdAt;
}

@Data
@Builder
public class SearchResponse {
    private String query;
    private List<MessageResponse> results;
    private Integer total;
}

@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
}

@Data
@Builder
public class ModelInfo {
    private String id;
    private String name;
    private String provider;
    private String description;
    private boolean available;
}
```

### 2. GraphQL API

#### 技术讲解
GraphQL是一种用于API的查询语言和运行时，它提供了一种更高效、强大和灵活的替代REST的方法。

**主要特点：**
- 精确数据获取：客户端可以准确指定需要的数据
- 单一端点：所有操作通过一个URL进行
- 强类型系统：明确定义API的能力
- 实时订阅：支持实时数据更新
- 内省：API是自文档化的

#### Demo示例

```java
// GraphQL配置
@Configuration
@EnableGraphQL
public class GraphQLConfig {
    
    @Bean
    public DataFetcher<List<Conversation>> conversationsDataFetcher(ConversationService conversationService) {
        return environment -> {
            String userId = environment.getArgument("userId");
            return conversationService.getUserConversations(userId);
        };
    }
    
    @Bean
    public DataFetcher<List<Message>> messagesDataFetcher(ConversationService conversationService) {
        return environment -> {
            String sessionId = environment.getArgument("sessionId");
            return conversationService.getConversationHistory(sessionId);
        };
    }
}

// GraphQL Schema (schema.graphqls)
/*
type Query {
    conversations(userId: String!): [Conversation!]!
    conversation(sessionId: String!): Conversation
    messages(sessionId: String!): [Message!]!
    searchMessages(query: String!, limit: Int = 10): [Message!]!
    models: [Model!]!
}

type Mutation {
    createConversation(input: CreateConversationInput!): Conversation!
    sendMessage(input: SendMessageInput!): Message!
    deleteConversation(sessionId: String!): Boolean!
}

type Subscription {
    messageAdded(sessionId: String!): Message!
    conversationUpdated(userId: String!): Conversation!
}

type Conversation {
    id: ID!
    sessionId: String!
    title: String
    userId: String!
    createdAt: String!
    updatedAt: String!
    messages: [Message!]!
}

type Message {
    id: ID!
    role: MessageRole!
    content: String!
    model: String
    tokenUsage: Int
    createdAt: String!
}

type Model {
    id: String!
    name: String!
    provider: String!
    description: String
    available: Boolean!
}

enum MessageRole {
    USER
    ASSISTANT
    SYSTEM
}

input CreateConversationInput {
    userId: String!
    title: String
}

input SendMessageInput {
    sessionId: String!
    content: String!
    model: String!
}
*/

// GraphQL控制器
@Controller
public class GraphQLController {
    
    private final ConversationService conversationService;
    private final AIService aiService;
    
    public GraphQLController(ConversationService conversationService, AIService aiService) {
        this.conversationService = conversationService;
        this.aiService = aiService;
    }
    
    @QueryMapping
    public List<Conversation> conversations(@Argument String userId) {
        return conversationService.getUserConversations(userId);
    }
    
    @QueryMapping
    public Conversation conversation(@Argument String sessionId) {
        return conversationService.getConversationBySessionId(sessionId);
    }
    
    @QueryMapping
    public List<Message> messages(@Argument String sessionId) {
        return conversationService.getConversationHistory(sessionId);
    }
    
    @QueryMapping
    public List<Message> searchMessages(@Argument String query, @Argument int limit) {
        return conversationService.searchMessages(query, limit);
    }
    
    @QueryMapping
    public List<ModelInfo> models() {
        return aiService.getAvailableModels();
    }
    
    @MutationMapping
    public Conversation createConversation(@Argument CreateConversationInput input) {
        return conversationService.createConversation(input.getUserId(), input.getTitle());
    }
    
    @MutationMapping
    public Message sendMessage(@Argument SendMessageInput input) {
        // 保存用户消息
        conversationService.addMessage(
            input.getSessionId(),
            MessageRole.USER,
            input.getContent(),
            null
        );
        
        // 生成AI回复
        String aiResponse = aiService.generateResponse(
            input.getContent(),
            input.getModel(),
            input.getSessionId()
        );
        
        // 保存AI消息
        return conversationService.addMessage(
            input.getSessionId(),
            MessageRole.ASSISTANT,
            aiResponse,
            input.getModel()
        );
    }
    
    @SubscriptionMapping
    public Flux<Message> messageAdded(@Argument String sessionId) {
        return conversationService.subscribeToMessages(sessionId);
    }
}

// GraphQL输入类型
@Data
public class CreateConversationInput {
    private String userId;
    private String title;
}

@Data
public class SendMessageInput {
    private String sessionId;
    private String content;
    private String model;
}
```

## 云平台技术

### 1. AWS (Amazon Web Services)

#### 技术讲解
AWS是亚马逊提供的云计算平台，提供计算、存储、数据库、机器学习等200多种服务。

**主要服务：**
- EC2：弹性计算云
- S3：简单存储服务
- RDS：关系数据库服务
- Lambda：无服务器计算
- Bedrock：AI模型服务

#### Demo示例

```java
// AWS配置
@Configuration
public class AWSConfig {
    
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    }
    
    @Bean
    public AWSLambda awsLambda() {
        return AWSLambdaClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    }
    
    @Bean
    public AmazonRDS amazonRDS() {
        return AmazonRDSClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    }
}

// S3文件服务
@Service
public class S3FileService {
    
    private final AmazonS3 s3Client;
    private final String bucketName = "ai-backend-files";
    
    public S3FileService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
        createBucketIfNotExists();
    }
    
    private void createBucketIfNotExists() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }
    }
    
    public String uploadFile(String fileName, InputStream inputStream, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType("application/octet-stream");
        
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
        s3Client.putObject(request);
        
        return s3Client.getUrl(bucketName, fileName).toString();
    }
    
    public InputStream downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        return s3Object.getObjectContent();
    }
    
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }
    
    public String generatePresignedUrl(String fileName, int expirationMinutes) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + expirationMinutes * 60 * 1000);
        
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
        
        return s3Client.generatePresignedUrl(request).toString();
    }
}

// Lambda函数调用服务
@Service
public class LambdaService {
    
    private final AWSLambda lambdaClient;
    private final ObjectMapper objectMapper;
    
    public LambdaService(AWSLambda lambdaClient) {
        this.lambdaClient = lambdaClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public <T> T invokeLambdaFunction(String functionName, Object payload, Class<T> responseType) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            InvokeRequest request = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(jsonPayload);
            
            InvokeResult result = lambdaClient.invoke(request);
            
            if (result.getStatusCode() == 200) {
                String responsePayload = new String(result.getPayload().array());
                return objectMapper.readValue(responsePayload, responseType);
            } else {
                throw new RuntimeException("Lambda函数调用失败，状态码: " + result.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Lambda函数调用异常", e);
        }
    }
    
    public void invokeLambdaAsync(String functionName, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            InvokeRequest request = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(jsonPayload)
                .withInvocationType(InvocationType.Event); // 异步调用
            
            lambdaClient.invoke(request);
        } catch (Exception e) {
            throw new RuntimeException("异步Lambda函数调用异常", e);
        }
    }
}

// AWS部署配置
// application-aws.yml
/*
cloud:
  aws:
    region:
      static: us-east-1
    credentials:
      access-key: ${AWS_ACCESS_KEY_ID}
      secret-key: ${AWS_SECRET_ACCESS_KEY}
    
spring:
  datasource:
    url: jdbc:postgresql://${RDS_ENDPOINT}:5432/${RDS_DB_NAME}
    username: ${RDS_USERNAME}
    password: ${RDS_PASSWORD}
  
  data:
    redis:
      host: ${ELASTICACHE_ENDPOINT}
      port: 6379

logging:
  level:
    com.amazonaws: DEBUG
*/
```

### 2. Azure

#### 技术讲解
Microsoft Azure是微软的云计算平台，提供计算、分析、存储和网络等云服务。

**主要服务：**
- Virtual Machines：虚拟机
- Blob Storage：对象存储
- SQL Database：托管数据库
- Functions：无服务器计算
- Cognitive Services：AI服务

#### Demo示例

```java
// Azure配置
@Configuration
public class AzureConfig {
    
    @Bean
    public BlobServiceClient blobServiceClient() {
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        return new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
    }
    
    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
            .endpoint(System.getenv("COSMOS_ENDPOINT"))
            .key(System.getenv("COSMOS_KEY"))
            .buildClient();
    }
}

// Azure Blob存储服务
@Service
public class AzureBlobService {
    
    private final BlobServiceClient blobServiceClient;
    private final String containerName = "ai-files";
    
    public AzureBlobService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
        createContainerIfNotExists();
    }
    
    private void createContainerIfNotExists() {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }
    }
    
    public String uploadBlob(String blobName, InputStream data, long length) {
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(blobName);
        
        blobClient.upload(data, length, true);
        return blobClient.getBlobUrl();
    }
    
    public InputStream downloadBlob(String blobName) {
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(blobName);
        
        return blobClient.openInputStream();
    }
    
    public void deleteBlob(String blobName) {
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(blobName);
        
        blobClient.delete();
    }
    
    public String generateSasUrl(String blobName, int expirationHours) {
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(blobName);
        
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(expirationHours);
        
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission);
        
        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(values);
    }
}

// Azure Functions集成
@Service
public class AzureFunctionService {
    
    private final RestTemplate restTemplate;
    
    public AzureFunctionService() {
        this.restTemplate = new RestTemplate();
    }
    
    public <T> T callFunction(String functionUrl, Object payload, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-functions-key", System.getenv("AZURE_FUNCTION_KEY"));
        
        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);
        
        ResponseEntity<T> response = restTemplate.postForEntity(functionUrl, entity, responseType);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Azure Function调用失败: " + response.getStatusCode());
        }
    }
}
```

### 3. Google Cloud Platform (GCP)

#### 技术讲解
Google Cloud Platform是谷歌提供的云计算服务套件，包括计算、存储、大数据、机器学习等服务。

**主要服务：**
- Compute Engine：虚拟机
- Cloud Storage：对象存储
- Cloud SQL：托管数据库
- Cloud Functions：无服务器计算
- Vertex AI：机器学习平台

#### Demo示例

```java
// GCP配置
@Configuration
public class GCPConfig {
    
    @Bean
    public Storage storage() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        return StorageOptions.newBuilder()
            .setCredentials(credentials)
            .build()
            .getService();
    }
    
    @Bean
    public Firestore firestore() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirestoreOptions options = FirestoreOptions.newBuilder()
            .setCredentials(credentials)
            .build();
        return options.getService();
    }
}

// Google Cloud Storage服务
@Service
public class GCSService {
    
    private final Storage storage;
    private final String bucketName = "ai-backend-storage";
    
    public GCSService(Storage storage) {
        this.storage = storage;
        createBucketIfNotExists();
    }
    
    private void createBucketIfNotExists() {
        Bucket bucket = storage.get(bucketName);
        if (bucket == null) {
            storage.create(BucketInfo.of(bucketName));
        }
    }
    
    public String uploadFile(String fileName, byte[] content, String contentType) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .build();
        
        Blob blob = storage.create(blobInfo, content);
        return String.format("gs://%s/%s", bucketName, fileName);
    }
    
    public byte[] downloadFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new RuntimeException("文件不存在: " + fileName);
        }
        
        return blob.getContent();
    }
    
    public void deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }
    
    public String generateSignedUrl(String fileName, int expirationMinutes) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        
        URL signedUrl = storage.signUrl(
            BlobInfo.newBuilder(blobId).build(),
            expirationMinutes,
            TimeUnit.MINUTES,
            Storage.SignUrlOption.withV4Signature()
        );
        
        return signedUrl.toString();
    }
}

// Firestore数据库服务
@Service
public class FirestoreService {
    
    private final Firestore firestore;
    
    public FirestoreService(Firestore firestore) {
        this.firestore = firestore;
    }
    
    public void saveDocument(String collection, String documentId, Map<String, Object> data) {
        try {
            firestore.collection(collection).document(documentId).set(data).get();
        } catch (Exception e) {
            throw new RuntimeException("保存文档失败", e);
        }
    }
    
    public Map<String, Object> getDocument(String collection, String documentId) {
        try {
            DocumentSnapshot document = firestore.collection(collection)
                .document(documentId)
                .get()
                .get();
            
            if (document.exists()) {
                return document.getData();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("获取文档失败", e);
        }
    }
    
    public List<Map<String, Object>> queryDocuments(String collection, String field, Object value) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(collection)
                .whereEqualTo(field, value)
                .get()
                .get();
            
            return querySnapshot.getDocuments().stream()
                .map(DocumentSnapshot::getData)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("查询文档失败", e);
        }
    }
    
    public void deleteDocument(String collection, String documentId) {
        try {
            firestore.collection(collection).document(documentId).delete().get();
        } catch (Exception e) {
            throw new RuntimeException("删除文档失败", e);
        }
    }
}
```

## 云平台部署配置

### Docker容器化

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ai-backend-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Kubernetes部署

```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-backend
  labels:
    app: ai-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ai-backend
  template:
    metadata:
      labels:
        app: ai-backend
    spec:
      containers:
      - name: ai-backend
        image: ai-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /api/v1/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/v1/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: ai-backend-service
spec:
  selector:
    app: ai-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

这份API和云平台技术文档涵盖了REST API、GraphQL以及主要云平台（AWS、Azure、GCP）的集成方法，提供了完整的云原生应用开发指南。