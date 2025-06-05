#!/bin/bash

# RingCentral MultiAgent System - 完整构建脚本
# 作者: RingCentral AI Team
# 版本: 1.0.0

set -e

echo "🚀 开始构建 RingCentral MultiAgent System..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 检查环境
check_environment() {
    log_info "检查构建环境..."
    
    # 检查Java版本
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请安装Java 17+"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" -lt 17 ]; then
        log_error "Java版本过低，需要Java 17+，当前版本: $java_version"
        exit 1
    fi
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_warning "Docker未安装，将跳过Docker镜像构建"
        SKIP_DOCKER=true
    fi
    
    log_success "环境检查完成"
}

# 清理构建目录
clean_build() {
    log_info "清理构建目录..."
    ./gradlew clean
    log_success "构建目录清理完成"
}

# 编译所有模块
compile_all() {
    log_info "编译所有模块..."
    ./gradlew compileJava compileTestJava
    log_success "所有模块编译完成"
}

# 运行测试
run_tests() {
    log_info "运行单元测试..."
    ./gradlew :tests:unit-tests:test
    
    log_info "运行集成测试..."
    ./gradlew :tests:integration-tests:test
    
    log_success "测试执行完成"
}

# 代码质量检查
quality_check() {
    log_info "执行代码质量检查..."
    
    # JaCoCo测试覆盖率
    ./gradlew jacocoTestReport
    
    # SonarQube分析 (如果配置了)
    if [ -n "$SONAR_TOKEN" ]; then
        ./gradlew sonarqube
        log_success "SonarQube分析完成"
    else
        log_warning "未配置SONAR_TOKEN，跳过SonarQube分析"
    fi
    
    log_success "代码质量检查完成"
}

# 构建JAR包
build_jars() {
    log_info "构建JAR包..."
    ./gradlew bootJar
    log_success "JAR包构建完成"
}

# 构建Docker镜像
build_docker() {
    if [ "$SKIP_DOCKER" = true ]; then
        log_warning "跳过Docker镜像构建"
        return
    fi
    
    log_info "构建Docker镜像..."
    ./gradlew dockerBuildAll
    log_success "Docker镜像构建完成"
}

# 生成构建报告
generate_report() {
    log_info "生成构建报告..."
    
    # 创建报告目录
    mkdir -p build/reports/build-summary
    
    # 生成构建摘要
    cat > build/reports/build-summary/build-summary.md << EOF
# RingCentral MultiAgent System 构建报告

## 构建信息
- 构建时间: $(date)
- 构建版本: $(./gradlew properties -q | grep "version:" | awk '{print $2}')
- Git提交: $(git rev-parse --short HEAD 2>/dev/null || echo "N/A")
- 构建环境: $(uname -s) $(uname -m)

## 模块列表
### 共享模块
- shared
- infrastructure

### 平台服务
- platform-services/api-gateway
- platform-services/auth-service
- platform-services/config-service
- platform-services/monitor-service

### AI引擎
- ai-engines/speech-engine
- ai-engines/nlu-engine
- ai-engines/knowledge-engine
- ai-engines/reasoning-engine

### 智能体服务
- agent-services/meeting-agent
- agent-services/call-agent
- agent-services/router-agent
- agent-services/analytics-agent

### 测试模块
- tests/unit-tests
- tests/integration-tests
- tests/e2e-tests
- tests/performance-tests

## 构建结果
- ✅ 编译成功
- ✅ 测试通过
- ✅ 代码质量检查通过
- ✅ JAR包构建完成
$([ "$SKIP_DOCKER" != true ] && echo "- ✅ Docker镜像构建完成" || echo "- ⚠️ Docker镜像构建跳过")

## 产物位置
- JAR包: \`*/build/libs/*.jar\`
- 测试报告: \`build/reports/tests/\`
- 覆盖率报告: \`build/reports/jacoco/\`
$([ "$SKIP_DOCKER" != true ] && echo "- Docker镜像: 本地Docker仓库")
EOF
    
    log_success "构建报告生成完成: build/reports/build-summary/build-summary.md"
}

# 主函数
main() {
    echo "========================================"
    echo "  RingCentral MultiAgent System Build  "
    echo "========================================"
    
    check_environment
    clean_build
    compile_all
    run_tests
    quality_check
    build_jars
    build_docker
    generate_report
    
    echo "========================================"
    log_success "🎉 构建完成！"
    echo "========================================"
    
    # 显示构建摘要
    echo ""
    log_info "构建摘要:"
    echo "- 📦 JAR包数量: $(find . -name "*.jar" -path "*/build/libs/*" | wc -l)"
    echo "- 🧪 测试用例: $(find . -name "TEST-*.xml" -path "*/build/test-results/*" | xargs grep -l "testcase" | wc -l) 个文件"
    echo "- 📊 覆盖率报告: build/reports/jacoco/"
    if [ "$SKIP_DOCKER" != true ]; then
        echo "- 🐳 Docker镜像: $(docker images | grep ringcentral | wc -l) 个"
    fi
    echo "- 📋 详细报告: build/reports/build-summary/build-summary.md"
}

# 执行主函数
main "$@" 