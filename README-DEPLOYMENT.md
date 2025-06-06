# RingCentral MultiAgent System - 部署指南

## 🚀 快速开始

### **一键部署（推荐）**
```bash
# 运行自动化部署脚本
scripts\auto-deploy.bat
```

选择部署方式：
- **选项1**: 部署到本地Docker（快速测试）
- **选项2**: 部署到本地Kubernetes（完整开发环境）

## 📋 部署方式对比

| 特性 | Docker Compose | 本地Kubernetes |
|------|----------------|----------------|
| **启动速度** | ⚡ 快速（2-3分钟） | 🐌 较慢（5-8分钟） |
| **资源消耗** | 💚 低 | 💛 中等 |
| **功能完整性** | 🔶 基础功能 | ✅ 完整功能 |
| **访问地址** | http://localhost:8080 | http://dev-api.ringcentral.local |
| **适用场景** | 快速测试、开发调试 | 生产模拟、完整测试 |

## 🛠️ 手动部署

### **方式1: Docker Compose部署**
```bash
# 1. 运行测试和构建
scripts\deploy-to-docker.bat

# 2. 访问服务
# API Gateway: http://localhost:8080
# Auth Service: http://localhost:8081
```

### **方式2: Kubernetes部署**
```bash
# 1. 设置K8s环境（首次运行）
scripts\setup-local-k8s.bat

# 2. 部署应用
scripts\deploy-to-local-k8s.bat

# 3. 访问服务
# API Gateway: http://dev-api.ringcentral.local
# Auth Service: http://dev-auth.ringcentral.local
```

## 🔧 配置文件说明

### **配置文件位置**
```
config/
├── local-k8s-config.yml      # K8s部署配置
├── docker-compose-config.yml # Docker部署配置
└── ...

k8s/
├── infrastructure.yaml        # 基础设施配置
├── services.yaml             # 应用服务配置
└── ingress.yaml              # 网关配置
```

### **修改配置**
1. **端口配置**: 编辑 `config/docker-compose-config.yml`
2. **域名配置**: 编辑 `config/local-k8s-config.yml`
3. **服务配置**: 编辑 `k8s/*.yaml` 文件

## 🌐 访问地址

### **Docker部署访问地址**
- 🌐 **API Gateway**: http://localhost:8080
- 🔐 **Auth Service**: http://localhost:8081
- 📞 **Meeting Agent**: http://localhost:8082
- 📱 **Call Agent**: http://localhost:8083

### **Kubernetes部署访问地址**
- 🌐 **API Gateway**: http://dev-api.ringcentral.local
- 🔐 **Auth Service**: http://dev-auth.ringcentral.local
- 📞 **Meeting Agent**: http://dev-meeting.ringcentral.local
- 📱 **Call Agent**: http://dev-call.ringcentral.local

## 🧪 CI/CD自动化

### **代码提交触发部署**
```bash
# 推送到develop分支 → 自动部署开发环境
git add .
git commit -m "feat: 新功能开发"
git push origin develop

# 推送到main分支 → 需审批后部署生产环境
git push origin main
```

### **GitHub Actions流程**
1. **代码检查**: 代码质量分析
2. **自动测试**: 单元测试 + 集成测试
3. **镜像构建**: Docker镜像构建
4. **自动部署**: 部署到目标环境
5. **健康检查**: 服务可用性验证

## 🔍 故障排查

### **常见问题**

#### **1. Docker相关**
```bash
# 问题: Docker未启动
# 解决: 启动Docker Desktop

# 问题: 端口冲突
# 解决: 修改config/docker-compose-config.yml中的端口配置
```

#### **2. Kubernetes相关**
```bash
# 问题: kubectl命令不可用
# 解决: 安装kubectl或启用Docker Desktop K8s

# 问题: 域名无法访问
# 解决: 检查hosts文件是否正确配置

# 问题: Ingress Controller未安装
# 解决: 重新运行 scripts\setup-local-k8s.bat
```

#### **3. 服务启动失败**
```bash
# 查看Docker日志
docker-compose -f docker-compose.dev.yml logs [service-name]

# 查看K8s日志
kubectl logs -f deployment/api-gateway -n ringcentral-dev

# 查看服务状态
kubectl get pods -n ringcentral-dev
```

## 📊 监控和管理

### **Docker环境管理**
```bash
# 查看运行状态
docker-compose -f docker-compose.dev.yml ps

# 查看日志
docker-compose -f docker-compose.dev.yml logs -f

# 停止服务
docker-compose -f docker-compose.dev.yml down

# 重启服务
docker-compose -f docker-compose.dev.yml restart
```

### **Kubernetes环境管理**
```bash
# 查看所有资源
kubectl get all -n ringcentral-dev

# 查看Pod状态
kubectl get pods -n ringcentral-dev -o wide

# 查看服务
kubectl get services -n ringcentral-dev

# 查看Ingress
kubectl get ingress -n ringcentral-dev

# 端口转发（备用访问方式）
kubectl port-forward svc/api-gateway 8080:80 -n ringcentral-dev
```

## 🎯 性能优化

### **构建优化**
- ✅ 使用Gradle并行构建
- ✅ 启用构建缓存
- ✅ 配置缓存优化

### **运行时优化**
- ✅ 容器健康检查
- ✅ 资源限制配置
- ✅ 自动重启策略

## 📚 相关文档

- [部署目标配置选项](docs/deployment/DEPLOYMENT_TARGET_OPTIONS.md)
- [完整部署指南](docs/deployment/DEPLOYMENT_GUIDE.md)
- [构建优化总结](BUILD_OPTIMIZATION_SUMMARY.md)

## 🆘 获取帮助

如果遇到问题，请：
1. 查看本文档的故障排查部分
2. 检查相关日志输出
3. 确认环境配置是否正确
4. 联系开发团队获取支持 