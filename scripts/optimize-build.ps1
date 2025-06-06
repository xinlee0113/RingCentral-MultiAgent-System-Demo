#!/usr/bin/env pwsh

# RingCentral多智能体系统 - 构建优化脚本
# 作者: AI Assistant
# 版本: 1.0.0

param(
    [switch]$Clean,
    [switch]$Profile,
    [switch]$Parallel,
    [switch]$SkipTests,
    [string]$Module = ""
)

Write-Host "🚀 RingCentral多智能体系统 - 构建优化脚本" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# 设置错误处理
$ErrorActionPreference = "Stop"

# 检查Java环境
function Test-JavaEnvironment {
    Write-Host "🔍 检查Java环境..." -ForegroundColor Yellow
    
    try {
        $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
        Write-Host "✅ Java版本: $javaVersion" -ForegroundColor Green
        
        if ($javaVersion -notmatch "17|18|19|20|21") {
            Write-Warning "⚠️  建议使用Java 17或更高版本以获得最佳性能"
        }
    }
    catch {
        Write-Error "❌ 未找到Java环境，请确保Java已正确安装并配置PATH"
        exit 1
    }
}

# 清理构建缓存
function Clear-BuildCache {
    Write-Host "🧹 清理构建缓存..." -ForegroundColor Yellow
    
    # 清理Gradle缓存
    if (Test-Path "$env:USERPROFILE\.gradle\caches") {
        Write-Host "  清理Gradle缓存目录..."
        Remove-Item "$env:USERPROFILE\.gradle\caches\*" -Recurse -Force -ErrorAction SilentlyContinue
    }
    
    # 清理项目构建目录
    Get-ChildItem -Path . -Name "build" -Directory -Recurse | ForEach-Object {
        Write-Host "  清理: $($_.FullName)"
        Remove-Item $_.FullName -Recurse -Force -ErrorAction SilentlyContinue
    }
    
    Write-Host "✅ 构建缓存清理完成" -ForegroundColor Green
}

# 优化系统设置
function Optimize-SystemSettings {
    Write-Host "⚙️  优化系统设置..." -ForegroundColor Yellow
    
    # 设置环境变量
    $env:GRADLE_OPTS = "-Xmx6g -XX:+UseG1GC -XX:+UseStringDeduplication"
    $env:JAVA_OPTS = "-Xmx4g -XX:+UseG1GC"
    
    Write-Host "✅ 系统设置优化完成" -ForegroundColor Green
}

# 执行构建
function Start-OptimizedBuild {
    param(
        [string]$Target = "build",
        [bool]$UseProfile = $false,
        [bool]$UseParallel = $true,
        [bool]$SkipTests = $false,
        [string]$SpecificModule = ""
    )
    
    Write-Host "🔨 开始优化构建..." -ForegroundColor Yellow
    
    $buildArgs = @()
    
    # 构建目标
    if ($SpecificModule) {
        $buildArgs += ":$SpecificModule`:$Target"
        Write-Host "  构建模块: $SpecificModule" -ForegroundColor Cyan
    } else {
        $buildArgs += $Target
        Write-Host "  构建目标: 所有模块" -ForegroundColor Cyan
    }
    
    # 性能选项
    if ($UseParallel) {
        $buildArgs += "--parallel"
        Write-Host "  并行构建: 启用" -ForegroundColor Cyan
    }
    
    if ($UseProfile) {
        $buildArgs += "--profile"
        $buildArgs += "--scan"
        Write-Host "  性能分析: 启用" -ForegroundColor Cyan
    }
    
    if ($SkipTests) {
        $buildArgs += "-x", "test"
        Write-Host "  跳过测试: 启用" -ForegroundColor Cyan
    }
    
    # 其他优化选项
    $buildArgs += "--build-cache"
    $buildArgs += "--configuration-cache"
    $buildArgs += "--daemon"
    
    Write-Host "  构建参数: $($buildArgs -join ' ')" -ForegroundColor Cyan
    
    # 记录开始时间
    $startTime = Get-Date
    Write-Host "  开始时间: $($startTime.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Cyan
    
    try {
        # 执行构建
        & ./gradlew @buildArgs
        
        if ($LASTEXITCODE -eq 0) {
            $endTime = Get-Date
            $duration = $endTime - $startTime
            Write-Host "✅ 构建成功完成!" -ForegroundColor Green
            Write-Host "  结束时间: $($endTime.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Green
            Write-Host "  构建耗时: $($duration.ToString('hh\:mm\:ss'))" -ForegroundColor Green
            
            # 显示构建报告位置
            if ($UseProfile) {
                $reportPath = "build/reports/profile"
                if (Test-Path $reportPath) {
                    Write-Host "📊 性能报告: file:///$((Get-Location).Path)/$reportPath" -ForegroundColor Cyan
                }
            }
        } else {
            Write-Error "❌ 构建失败，退出码: $LASTEXITCODE"
            exit $LASTEXITCODE
        }
    }
    catch {
        Write-Error "❌ 构建过程中发生错误: $($_.Exception.Message)"
        exit 1
    }
}

# 显示构建统计
function Show-BuildStats {
    Write-Host "📊 构建统计信息:" -ForegroundColor Yellow
    
    # 统计模块数量
    $moduleCount = (Get-ChildItem -Path . -Name "build.gradle.kts" -Recurse).Count
    Write-Host "  总模块数: $moduleCount" -ForegroundColor Cyan
    
    # 统计代码行数
    $javaFiles = Get-ChildItem -Path . -Name "*.java" -Recurse
    $kotlinFiles = Get-ChildItem -Path . -Name "*.kt" -Recurse
    $totalFiles = $javaFiles.Count + $kotlinFiles.Count
    Write-Host "  源文件数: $totalFiles (Java: $($javaFiles.Count), Kotlin: $($kotlinFiles.Count))" -ForegroundColor Cyan
    
    # 检查构建缓存大小
    if (Test-Path "$env:USERPROFILE\.gradle\caches") {
        $cacheSize = (Get-ChildItem "$env:USERPROFILE\.gradle\caches" -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
        Write-Host "  Gradle缓存: $([math]::Round($cacheSize, 2)) MB" -ForegroundColor Cyan
    }
}

# 主执行流程
try {
    Test-JavaEnvironment
    Optimize-SystemSettings
    
    if ($Clean) {
        Clear-BuildCache
    }
    
    Start-OptimizedBuild -UseProfile:$Profile -UseParallel:$Parallel -SkipTests:$SkipTests -SpecificModule:$Module
    
    Show-BuildStats
    
    Write-Host "🎉 构建优化脚本执行完成!" -ForegroundColor Green
}
catch {
    Write-Error "❌ 脚本执行失败: $($_.Exception.Message)"
    exit 1
} 