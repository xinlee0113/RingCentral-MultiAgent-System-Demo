# RingCentral MultiAgent System - 部署目标配置指南

## 🎯 部署选项概览

### **选项1: 本地Docker Compose（最简单）**
```bash
# 适用于：本地开发测试
# 访问地址：http://localhost:8080
# 优点：简单快速，无需K8s
# 缺点：单机，无高可用
```

### **选项2: 本地Kubernetes（推荐开发）**
```bash
# 适用于：本地开发，模拟生产环境
# 工具：Docker Desktop K8s / Minikube / Kind
# 访问地址：http://localhost:30080 或 http://dev.local
# 优点：完整K8s功能，本地控制
# 缺点：资源消耗大
```

### **选项3: 云端Kubernetes（推荐生产）**
```bash
# 适用于：生产环境，团队协作
# 平台：AWS EKS / Azure AKS / Google GKE / 阿里云ACK
# 访问地址：https://dev-api.ringcentral.com
# 优点：高可用，自动扩缩容，专业运维
# 缺点：成本较高
```

## 🔧 具体配置方案

### **方案A: 本地Docker Compose部署**

#### 1. 创建docker-compose.yml
```yaml
version: '3.8'
services:
  # 基础设施
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ringcentral_dev
      POSTGRES_USER: ringcentral
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  # 核心服务
  api-gateway:
    image: ringcentral/api-gateway:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DATABASE_URL=jdbc:postgresql://postgres:5432/ringcentral_dev
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis

  auth-service:
    image: ringcentral/auth-service:latest
    ports:
      - "8081:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DATABASE_URL=jdbc:postgresql://postgres:5432/ringcentral_dev
    depends_on:
      - postgres

volumes:
  postgres_data:
```

#### 2. 启动命令
```bash
# 构建并启动所有服务
docker-compose up -d

# 访问地址
echo "API Gateway: http://localhost:8080"
echo "Auth Service: http://localhost:8081"
```

### **方案B: 本地Kubernetes部署**

#### 1. 启用Docker Desktop Kubernetes
```bash
# Windows/Mac: Docker Desktop -> Settings -> Kubernetes -> Enable
# 或安装Minikube
minikube start --memory=8192 --cpus=4
```

#### 2. 配置本地域名（可选）
```bash
# Windows: 编辑 C:\Windows\System32\drivers\etc\hosts
# Mac/Linux: 编辑 /etc/hosts
127.0.0.1 dev-api.ringcentral.local
127.0.0.1 dev-auth.ringcentral.local
```

#### 3. 部署到本地K8s
```bash
# 设置本地K8s上下文
kubectl config use-context docker-desktop

# 部署到本地
KUBECONFIG=$HOME/.kube/config ./scripts/deploy-dev.sh

# 端口转发访问
kubectl port-forward -n ringcentral-dev svc/api-gateway 8080:80
```

### **方案C: 云端Kubernetes部署**

#### 1. AWS EKS配置示例
```bash
# 创建EKS集群
eksctl create cluster --name ringcentral-cluster --region us-west-2

# 获取kubeconfig
aws eks update-kubeconfig --region us-west-2 --name ringcentral-cluster

# 配置GitHub Secrets
# KUBE_CONFIG_DEV: base64编码的kubeconfig内容
cat ~/.kube/config | base64 -w 0
```

#### 2. 域名和SSL配置
```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ringcentral-ingress
  namespace: ringcentral-dev
  annotations:
    kubernetes.io/ingress.class: "nginx"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - dev-api.ringcentral.com
    secretName: ringcentral-tls
  rules:
  - host: dev-api.ringcentral.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-gateway
            port:
              number: 80
```

## 🚀 推荐配置流程

### **第一步：选择部署方式**
```bash
# 开发阶段：本地Docker Compose
# 测试阶段：本地Kubernetes  
# 生产阶段：云端Kubernetes
```

### **第二步：配置GitHub Secrets**
```bash
# 必需的Secrets：
DOCKER_REGISTRY=your-registry.com
KUBE_CONFIG_DEV=<base64-encoded-kubeconfig>
KUBE_CONFIG_PROD=<base64-encoded-kubeconfig>
SONAR_TOKEN=<sonarqube-token>
```

### **第三步：修改访问地址**
根据选择的部署方式，修改以下文件中的访问地址：
- `application-dev.yml`：开发环境配置
- `application-prod.yml`：生产环境配置
- `scripts/deploy-dev.sh`：部署脚本
- `.github/workflows/ci.yml`：CI/CD配置

## 📋 访问地址总结

| 部署方式 | 开发环境访问地址 | 生产环境访问地址 |
|----------|------------------|------------------|
| 本地Docker | http://localhost:8080 | - |
| 本地K8s | http://localhost:30080 | - |
| 云端K8s | https://dev-api.ringcentral.com | https://api.ringcentral.com |

## ⚠️ 当前状态说明

**目前的CI/CD配置是"半成品"状态**：
- ✅ 构建流程完整
- ✅ 部署脚本完整  
- ❌ 缺少具体的Kubernetes集群配置
- ❌ 缺少GitHub Secrets配置
- ❌ 缺少域名和SSL配置

**需要您决定**：
1. 使用哪种部署方式？
2. 如果用云端，选择哪个云平台？
3. 域名是否已准备好？

确定后，我可以帮您完成具体的配置。 