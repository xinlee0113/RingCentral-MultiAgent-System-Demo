@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo 🚀 设置 RingCentral MultiAgent System 本地Kubernetes环境...

REM 检查Docker Desktop是否运行
echo 📋 检查Docker Desktop...
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Desktop未运行，请先启动Docker Desktop
    echo 💡 启动步骤：打开Docker Desktop -^> 等待启动完成
    pause
    exit /b 1
)
echo ✅ Docker Desktop运行正常

REM 检查Kubernetes是否启用
echo 📋 检查Kubernetes...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Kubernetes未启用
    echo 💡 请手动启用：Docker Desktop -^> Settings -^> Kubernetes -^> Enable Kubernetes
    echo ⏳ 启用后请重新运行此脚本
    pause
    exit /b 1
)
echo ✅ Kubernetes集群运行正常

REM 检查当前上下文
for /f "tokens=*" %%i in ('kubectl config current-context') do set CURRENT_CONTEXT=%%i
echo 📍 当前Kubernetes上下文: %CURRENT_CONTEXT%

if not "%CURRENT_CONTEXT%"=="docker-desktop" (
    echo 🔄 切换到docker-desktop上下文...
    kubectl config use-context docker-desktop
    echo ✅ 已切换到docker-desktop上下文
)

REM 创建命名空间
echo 📦 创建命名空间...
kubectl create namespace ringcentral-dev --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace ringcentral-prod --dry-run=client -o yaml | kubectl apply -f -
echo ✅ 命名空间创建完成

REM 检查NGINX Ingress Controller
echo 🌐 检查NGINX Ingress Controller...
kubectl get pods -n ingress-nginx >nul 2>&1
if %errorlevel% neq 0 (
    echo 📥 安装NGINX Ingress Controller...
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
    
    echo ⏳ 等待Ingress Controller启动...
    kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=300s
    echo ✅ NGINX Ingress Controller安装完成
) else (
    echo ✅ NGINX Ingress Controller已存在
)

REM 配置本地域名解析
echo 🌍 配置本地域名解析...
set HOSTS_FILE=C:\Windows\System32\drivers\etc\hosts

REM 检查是否需要更新hosts文件
findstr "dev-api.ringcentral.local" "%HOSTS_FILE%" >nul 2>&1
if %errorlevel% neq 0 (
    echo 📝 更新hosts文件...
    echo ⚠️  需要管理员权限
    
    REM 尝试添加域名到hosts文件
    echo 127.0.0.1 dev-api.ringcentral.local >> "%HOSTS_FILE%" 2>nul
    echo 127.0.0.1 dev-auth.ringcentral.local >> "%HOSTS_FILE%" 2>nul
    echo 127.0.0.1 dev-meeting.ringcentral.local >> "%HOSTS_FILE%" 2>nul
    echo 127.0.0.1 dev-call.ringcentral.local >> "%HOSTS_FILE%" 2>nul
    
    if %errorlevel% neq 0 (
        echo ❌ 无法更新hosts文件，请以管理员身份运行
        echo 💡 或手动添加以下内容到 %HOSTS_FILE% :
        echo    127.0.0.1 dev-api.ringcentral.local
        echo    127.0.0.1 dev-auth.ringcentral.local
        echo    127.0.0.1 dev-meeting.ringcentral.local
        echo    127.0.0.1 dev-call.ringcentral.local
    ) else (
        echo ✅ hosts文件更新完成
    )
) else (
    echo ✅ hosts文件已配置
)

REM 创建本地镜像仓库Secret
echo 🔐 配置镜像仓库认证...
kubectl create secret docker-registry regcred --docker-server=docker.io --docker-username=ringcentral --docker-password=dummy --docker-email=dev@ringcentral.com --namespace=ringcentral-dev --dry-run=client -o yaml | kubectl apply -f -
echo ✅ 镜像仓库认证配置完成

echo.
echo 🎉 本地Kubernetes环境设置完成!
echo.
echo 📊 环境信息:
kubectl version --client --short
echo   当前上下文: %CURRENT_CONTEXT%
echo   可用命名空间:
kubectl get namespaces | findstr ringcentral

echo.
echo 🌐 配置的本地域名:
echo   http://dev-api.ringcentral.local
echo   http://dev-auth.ringcentral.local
echo   http://dev-meeting.ringcentral.local
echo   http://dev-call.ringcentral.local

echo.
echo 🔧 常用命令:
echo   查看所有Pod: kubectl get pods -n ringcentral-dev
echo   查看服务: kubectl get services -n ringcentral-dev
echo   查看Ingress: kubectl get ingress -n ringcentral-dev
echo   查看日志: kubectl logs -f deployment/api-gateway -n ringcentral-dev

echo.
echo 📚 下一步: 运行 scripts\deploy-to-local-k8s.bat 部署应用
pause 