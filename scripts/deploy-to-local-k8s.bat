@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo 🚀 部署 RingCentral MultiAgent System 到本地Kubernetes...

REM 检查环境
echo 📋 检查部署环境...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Kubernetes环境异常，请先运行 scripts\setup-local-k8s.bat
    pause
    exit /b 1
)
echo ✅ Kubernetes环境检查通过

REM 运行测试
echo 🧪 运行自动化测试...
call gradlew.bat test --parallel --build-cache --configuration-cache
if %errorlevel% neq 0 (
    echo ❌ 单元测试失败，部署中止
    pause
    exit /b 1
)
echo ✅ 单元测试通过

REM 构建Docker镜像
echo 🔨 构建Docker镜像...
call gradlew.bat :platform-services:api-gateway:jib --parallel --build-cache --configuration-cache
call gradlew.bat :platform-services:auth-service:jib --parallel --build-cache --configuration-cache
echo ✅ 镜像构建完成

REM 部署到K8s
echo 🚀 部署到Kubernetes...
kubectl create namespace ringcentral-dev --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f k8s/
echo ✅ 部署完成

echo.
echo 🎉 部署成功! 访问地址: http://dev-api.ringcentral.local
pause 