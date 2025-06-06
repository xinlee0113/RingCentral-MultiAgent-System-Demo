@echo off
chcp 65001 >nul
echo.
echo 🚀 RingCentral多智能体系统 - 安全清理工具
echo ============================================
echo.

:start
echo 📋 可用选项：
echo   1. 安全清理 (推荐) - 只删除build目录，保护依赖缓存
echo   2. 检查依赖缓存状态
echo   3. 显示依赖信息
echo   4. 完整构建
echo   5. 退出
echo.

set /p choice=请选择操作 (1-5): 

if "%choice%"=="1" goto safeclean
if "%choice%"=="2" goto checkcache
if "%choice%"=="3" goto showdeps
if "%choice%"=="4" goto build
if "%choice%"=="5" goto exit
goto invalid

:safeclean
echo.
echo 🧹 执行安全清理...
echo ⚠️  注意：这只会删除build目录，不会删除依赖缓存
echo.
gradlew.bat safeclean
goto end

:checkcache
echo.
echo 📦 检查依赖缓存状态...
echo.
gradlew.bat checkDependencyCache
goto end

:showdeps
echo.
echo 📥 显示依赖信息...
echo.
gradlew.bat showDependencyDownload
goto end

:build
echo.
echo 🔨 开始完整构建...
echo.
gradlew.bat build
goto end

:invalid
echo.
echo ❌ 无效选择，请输入 1-5
echo.
pause
goto start

:end
echo.
echo ✅ 操作完成！
echo.
echo 💡 重要提示：
echo   • 依赖缓存位置: %USERPROFILE%\.gradle\caches\
echo   • 使用安全清理可以避免重新下载依赖
echo   • gradle clean 不会删除依赖缓存，但建议使用安全清理
echo.
pause
goto start

:exit
echo.
echo 👋 再见！
echo. 