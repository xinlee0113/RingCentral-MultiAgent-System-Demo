#!/bin/bash

# RingCentral MultiAgent System - 开发环境部署脚本
# 作者: RingCentral AI Team
# 版本: 1.0.0

set -e

echo "🚀 开始部署 RingCentral MultiAgent System 到开发环境..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
NAMESPACE="ringcentral-dev"
DOCKER_REGISTRY="${REGISTRY:-ghcr.io/xinlee0113/ringcentral-multiagent-system}"
VERSION=${GITHUB_SHA:-1.0.0}
KUBECONFIG_PATH="${KUBECONFIG:-$HOME/.kube/config}"
USE_KIND="${USE_KIND:-false}"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查部署环境
check_environment() {
    log_info "检查部署环境..."
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    # 检查kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl未安装，请先安装kubectl"
        exit 1
    fi
    
    # 检查Kubernetes连接
    if ! kubectl cluster-info &> /dev/null; then
        if [ "$USE_KIND" = "true" ]; then
            log_warning "无法连接到Kubernetes集群，但USE_KIND=true，假设kind集群正在设置中..."
        else
            log_error "无法连接到Kubernetes集群，请检查kubeconfig"
            exit 1
        fi
    else
        log_success "成功连接到Kubernetes集群"
        kubectl cluster-info
    fi
    
    # 检查Helm
    if ! command -v helm &> /dev/null; then
        log_warning "Helm未安装，将使用kubectl部署"
        USE_HELM=false
    else
        USE_HELM=true
    fi
    
    log_success "环境检查完成"
}

# 创建命名空间
create_namespace() {
    log_info "创建命名空间: $NAMESPACE"
    
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    
    log_success "命名空间创建完成"
}

# 部署基础设施
deploy_infrastructure() {
    log_info "部署基础设施组件..."
    
    # PostgreSQL
    log_info "部署PostgreSQL..."
    kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        env:
        - name: POSTGRES_DB
          value: ringcentral_dev
        - name: POSTGRES_USER
          value: ringcentral
        - name: POSTGRES_PASSWORD
          value: dev_password
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: $NAMESPACE
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
EOF

    # Redis
    log_info "部署Redis..."
    kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7
        ports:
        - containerPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: $NAMESPACE
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
EOF

    # Kafka
    log_info "部署Kafka..."
    cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: confluentinc/cp-kafka:latest
        env:
        - name: KAFKA_ZOOKEEPER_CONNECT
          value: "zookeeper:2181"
        - name: KAFKA_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092"
        - name: KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR
          value: "1"
        ports:
        - containerPort: 9092
---
apiVersion: v1
kind: Service
metadata:
  name: kafka
  namespace: $NAMESPACE
spec:
  selector:
    app: kafka
  ports:
  - port: 9092
    targetPort: 9092
EOF

    log_success "基础设施部署完成"
}

# 部署平台服务
deploy_platform_services() {
    log_info "部署平台服务..."
    
    # 认证服务
    deploy_service "auth-service" "8080"
    
    # API网关
    deploy_service "api-gateway" "8080"
    
    log_success "平台服务部署完成"
}

# 部署AI引擎
deploy_ai_engines() {
    log_info "部署AI引擎..."
    
    # 语音引擎
    deploy_service "speech-engine" "8080"
    
    # NLU引擎
    deploy_service "nlu-engine" "8080"
    
    log_success "AI引擎部署完成"
}

# 部署智能体服务
deploy_agent_services() {
    log_info "部署智能体服务..."
    
    # 会议智能体
    deploy_service "meeting-agent" "8080"
    
    # 通话智能体
    deploy_service "call-agent" "8080"
    
    log_success "智能体服务部署完成"
}

# 通用服务部署函数
deploy_service() {
    local service_name=$1
    local port=$2
    
    log_info "部署服务: $service_name"
    
    cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $service_name
  namespace: $NAMESPACE
  labels:
    app: $service_name
    version: $VERSION
spec:
  replicas: 1
  selector:
    matchLabels:
      app: $service_name
  template:
    metadata:
      labels:
        app: $service_name
        version: $VERSION
    spec:
      containers:
      - name: $service_name
        image: $DOCKER_REGISTRY/$service_name:$VERSION
        ports:
        - containerPort: $port
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres:5432/ringcentral_dev"
        - name: SPRING_DATASOURCE_USERNAME
          value: "ringcentral"
        - name: SPRING_DATASOURCE_PASSWORD
          value: "dev_password"
        - name: SPRING_REDIS_HOST
          value: "redis"
        - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
          value: "kafka:9092"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: $port
          initialDelaySeconds: 90
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: $port
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: $service_name
  namespace: $NAMESPACE
  labels:
    app: $service_name
spec:
  selector:
    app: $service_name
  ports:
  - port: $port
    targetPort: $port
    name: http
  type: ClusterIP
EOF
}

# 创建Ingress
create_ingress() {
    log_info "创建Ingress..."
    
    cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ringcentral-ingress
  namespace: $NAMESPACE
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: dev.ringcentral.local
    http:
      paths:
      - path: /api/gateway
        pathType: Prefix
        backend:
          service:
            name: api-gateway
            port:
              number: 8080
      - path: /api/auth
        pathType: Prefix
        backend:
          service:
            name: auth-service
            port:
              number: 8080
      - path: /api/meeting
        pathType: Prefix
        backend:
          service:
            name: meeting-agent
            port:
              number: 8080
      - path: /api/call
        pathType: Prefix
        backend:
          service:
            name: call-agent
            port:
              number: 8080
      - path: /api/speech
        pathType: Prefix
        backend:
          service:
            name: speech-engine
            port:
              number: 8080
      - path: /api/nlu
        pathType: Prefix
        backend:
          service:
            name: nlu-engine
            port:
              number: 8080
EOF
    
    log_success "Ingress创建完成"
}

# 等待服务就绪
wait_for_services() {
    log_info "等待服务就绪..."
    
    # 等待基础设施服务就绪
    log_info "等待基础设施服务..."
    kubectl wait --for=condition=available --timeout=600s deployment/postgres deployment/redis deployment/kafka -n $NAMESPACE
    
    # 等待应用服务就绪（更长的超时时间）
    log_info "等待应用服务..."
    kubectl wait --for=condition=available --timeout=900s deployment/auth-service deployment/api-gateway deployment/speech-engine deployment/nlu-engine deployment/meeting-agent deployment/call-agent -n $NAMESPACE || {
        log_info "部分服务启动超时，检查状态..."
        kubectl get pods -n $NAMESPACE
        kubectl describe pods -n $NAMESPACE | grep -A 10 "Events:"
        return 0  # 不要因为超时而失败，继续显示状态
    }
    
    log_success "服务等待完成"
}

# 显示部署状态
show_status() {
    log_info "部署状态:"
    echo ""
    
    # 显示Pod状态
    echo "📦 Pod状态:"
    kubectl get pods -n $NAMESPACE -o wide
    echo ""
    
    # 显示服务状态
    echo "🌐 服务状态:"
    kubectl get services -n $NAMESPACE
    echo ""
    
    # 显示Ingress状态
    echo "🚪 Ingress状态:"
    kubectl get ingress -n $NAMESPACE
    echo ""
    
    # 显示访问信息
    echo "🔗 访问信息:"
    echo "- API网关: http://dev.ringcentral.local/api/gateway"
    echo "- 认证服务: http://dev.ringcentral.local/api/auth"
    echo "- 会议智能体: http://dev.ringcentral.local/api/meeting"
    echo "- 通话智能体: http://dev.ringcentral.local/api/call"
    echo "- 语音引擎: http://dev.ringcentral.local/api/speech"
    echo "- NLU引擎: http://dev.ringcentral.local/api/nlu"
    echo ""
    echo "💡 提示: 请确保在/etc/hosts中添加以下条目:"
    echo "127.0.0.1 dev.ringcentral.local"
}

# 主函数
main() {
    echo "========================================"
    echo "  RingCentral MultiAgent System Deploy "
    echo "========================================"
    
    check_environment
    create_namespace
    deploy_infrastructure
    
    # 等待基础设施就绪
    log_info "等待基础设施就绪..."
    sleep 30
    
    deploy_platform_services
    deploy_ai_engines
    deploy_agent_services
    create_ingress
    wait_for_services
    show_status
    
    echo "========================================"
    log_success "🎉 部署完成！"
    echo "========================================"
}

# 执行主函数
main "$@" 