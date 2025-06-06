@echo off
chcp 65001 >nul

echo 🚀 RingCentral MultiAgent System - 自动化CI/CD部署
echo.

REM 显示部署选项
echo 请选择部署方式:
echo [1] 部署到本地Docker (推荐快速测试)
echo [2] 部署到本地Kubernetes (推荐开发环境)
echo [3] 退出
echo.

set /p choice=请输入选择 (1-3): 

if "%choice%"=="1" goto deploy_docker
if "%choice%"=="2" goto deploy_k8s
if "%choice%"=="3" goto exit
echo 无效选择，请重新运行脚本
pause
goto exit

:deploy_docker
echo.
echo 🐳 开始部署到本地Docker...
call scripts\deploy-to-docker.bat
goto end

:deploy_k8s
echo.
echo ☸️  开始部署到本地Kubernetes...
echo 📋 首先检查K8s环境是否已设置...

kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Kubernetes环境未设置，正在初始化...
    call scripts\setup-local-k8s.bat
    if %errorlevel% neq 0 (
        echo ❌ K8s环境设置失败
        pause
        goto exit
    )
)

echo ✅ K8s环境检查通过，开始部署...
call scripts\deploy-to-local-k8s.bat
goto end

:end
echo.
echo 🎉 自动化部署完成!
echo.
echo 💡 提示: 
echo   - Docker部署访问: http://localhost:8080
echo   - K8s部署访问: http://dev-api.ringcentral.local
echo   - 查看服务状态: docker ps 或 kubectl get pods -n ringcentral-dev
echo.

:exit
pause 