#!/bin/bash
# RingCentral多智能体系统 - 生产环境部署脚本

set -e

echo "🚀 开始部署到生产环境..."

# 环境变量
NAMESPACE="ringcentral-prod"
REGISTRY="ringcentral"
VERSION=${GITHUB_SHA:-latest}
KUBECONFIG_PATH=${KUBECONFIG_PATH:-~/.kube/config}

# 检查必要工具
command -v kubectl >/dev/null 2>&1 || { echo "❌ kubectl未安装"; exit 1; }
command -v helm >/dev/null 2>&1 || { echo "❌ helm未安装"; exit 1; }

echo "✅ 工具检查完成"

# 创建命名空间
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
echo "✅ 命名空间 $NAMESPACE 已准备"

# 部署高可用基础设施
echo "📦 部署生产级基础设施..."

# PostgreSQL集群 (使用StatefulSet)
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: $NAMESPACE
spec:
  serviceName: postgres
  replicas: 3
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
          value: ringcentral_prod
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: password
        - name: PGDATA
          value: /var/lib/postgresql/data/pgdata
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 100Gi
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
  clusterIP: None
---
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  namespace: $NAMESPACE
type: Opaque
data:
  username: cG9zdGdyZXM=  # postgres (base64)
  password: cHJvZF9wYXNzd29yZA==  # prod_password (base64)
EOF

# Redis集群
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
  namespace: $NAMESPACE
spec:
  serviceName: redis
  replicas: 3
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
        command:
        - redis-server
        - --cluster-enabled
        - "yes"
        - --cluster-config-file
        - nodes.conf
        - --cluster-node-timeout
        - "5000"
        - --appendonly
        - "yes"
        ports:
        - containerPort: 6379
        - containerPort: 16379
        volumeMounts:
        - name: redis-storage
          mountPath: /data
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
  volumeClaimTemplates:
  - metadata:
      name: redis-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 50Gi
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
  clusterIP: None
EOF

echo "✅ 基础设施服务部署完成"

# 等待基础设施就绪
echo "⏳ 等待基础设施服务启动..."
kubectl wait --for=condition=ready --timeout=600s pod -l app=postgres -n $NAMESPACE
kubectl wait --for=condition=ready --timeout=600s pod -l app=redis -n $NAMESPACE
echo "✅ 基础设施服务已就绪"

# 部署应用服务 (高可用配置)
echo "🔧 部署生产级应用服务..."

# API Gateway (高可用)
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: $NAMESPACE
spec:
  replicas: 5
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 2
      maxUnavailable: 1
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: $REGISTRY/api-gateway:$VERSION
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: DATABASE_URL
          value: jdbc:postgresql://postgres:5432/ringcentral_prod
        - name: REDIS_URL
          value: redis://redis:6379
        - name: JVM_OPTS
          value: "-Xmx2g -Xms1g -XX:+UseG1GC"
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: $NAMESPACE
spec:
  selector:
    app: api-gateway
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: api-gateway-hpa
  namespace: $NAMESPACE
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: api-gateway
  minReplicas: 5
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
EOF

# Auth Service (高可用)
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
  namespace: $NAMESPACE
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: $REGISTRY/auth-service:$VERSION
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: DATABASE_URL
          value: jdbc:postgresql://postgres:5432/ringcentral_prod
        - name: REDIS_URL
          value: redis://redis:6379
        - name: JVM_OPTS
          value: "-Xmx2g -Xms1g -XX:+UseG1GC"
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
  namespace: $NAMESPACE
spec:
  selector:
    app: auth-service
  ports:
  - port: 8081
    targetPort: 8081
EOF

# Meeting Agent (高可用)
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: meeting-agent
  namespace: $NAMESPACE
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: meeting-agent
  template:
    metadata:
      labels:
        app: meeting-agent
    spec:
      containers:
      - name: meeting-agent
        image: $REGISTRY/meeting-agent:$VERSION
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: API_GATEWAY_URL
          value: http://api-gateway
        - name: AUTH_SERVICE_URL
          value: http://auth-service:8081
        - name: JVM_OPTS
          value: "-Xmx2g -Xms1g -XX:+UseG1GC"
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8082
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8082
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: meeting-agent
  namespace: $NAMESPACE
spec:
  selector:
    app: meeting-agent
  ports:
  - port: 8082
    targetPort: 8082
EOF

echo "✅ 应用服务部署完成"

# 等待应用服务就绪
echo "⏳ 等待应用服务启动..."
kubectl wait --for=condition=available --timeout=900s deployment/api-gateway -n $NAMESPACE
kubectl wait --for=condition=available --timeout=900s deployment/auth-service -n $NAMESPACE
kubectl wait --for=condition=available --timeout=900s deployment/meeting-agent -n $NAMESPACE

echo "✅ 所有服务已就绪"

# 配置Ingress (生产级负载均衡)
kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ringcentral-ingress
  namespace: $NAMESPACE
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  tls:
  - hosts:
    - api.ringcentral.prod
    secretName: ringcentral-tls
  rules:
  - host: api.ringcentral.prod
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-gateway
            port:
              number: 80
EOF

# 获取访问地址
echo "🌐 获取服务访问地址..."
INGRESS_IP=$(kubectl get ingress ringcentral-ingress -n $NAMESPACE -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$INGRESS_IP" ]; then
    INGRESS_IP="待分配"
fi

echo "🎉 生产环境部署完成!"
echo "📋 服务信息:"
echo "   - 生产域名: https://api.ringcentral.prod"
echo "   - Ingress IP: $INGRESS_IP"
echo "   - 命名空间: $NAMESPACE"
echo "   - 版本: $VERSION"
echo "   - API Gateway副本数: 5 (自动扩缩容 5-20)"
echo "   - Auth Service副本数: 3"
echo "   - Meeting Agent副本数: 3"

# 健康检查
echo "🔍 执行健康检查..."
kubectl get pods -n $NAMESPACE
kubectl get services -n $NAMESPACE
kubectl get ingress -n $NAMESPACE

echo "✅ 生产环境部署成功!" 