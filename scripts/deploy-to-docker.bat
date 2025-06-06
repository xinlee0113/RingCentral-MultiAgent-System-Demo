@echo off
chcp 65001 >nul

echo 🚀 部署到本地Docker...

docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker未运行
    pause
    exit /b 1
)

call gradlew.bat test --parallel --build-cache --configuration-cache
if %errorlevel% neq 0 (
    echo ❌ 测试失败
    pause
    exit /b 1
)

call gradlew.bat :platform-services:api-gateway:jib --parallel
call gradlew.bat :platform-services:auth-service:jib --parallel

docker-compose -f docker-compose.dev.yml up -d

echo 🎉 部署成功! 访问: http://localhost:8080
pause 