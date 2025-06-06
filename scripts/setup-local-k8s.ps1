# RingCentral MultiAgent System - 本地Kubernetes环境设置脚本
# 作者: RingCentral AI Team
# 版本: 1.0.0

Write-Host "🚀 设置 RingCentral MultiAgent System 本地Kubernetes环境..." -ForegroundColor Green

# 检查Docker Desktop是否运行
Write-Host "📋 检查Docker Desktop..." -ForegroundColor Blue
try {
    docker version | Out-Null
    Write-Host "✅ Docker Desktop运行正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Desktop未运行，请先启动Docker Desktop" -ForegroundColor Red
    Write-Host "💡 启动步骤：打开Docker Desktop -> 等待启动完成" -ForegroundColor Yellow
    exit 1
}

# 检查Kubernetes是否启用
Write-Host "📋 检查Kubernetes..." -ForegroundColor Blue
try {
    kubectl cluster-info | Out-Null
    Write-Host "✅ Kubernetes集群运行正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Kubernetes未启用，正在启用..." -ForegroundColor Yellow
    Write-Host "💡 请手动启用：Docker Desktop -> Settings -> Kubernetes -> Enable Kubernetes" -ForegroundColor Yellow
    Write-Host "⏳ 启用后请重新运行此脚本" -ForegroundColor Yellow
    exit 1
}

# 检查当前上下文
$currentContext = kubectl config current-context
Write-Host "📍 当前Kubernetes上下文: $currentContext" -ForegroundColor Cyan

if ($currentContext -ne "docker-desktop") {
    Write-Host "🔄 切换到docker-desktop上下文..." -ForegroundColor Yellow
    kubectl config use-context docker-desktop
    Write-Host "✅ 已切换到docker-desktop上下文" -ForegroundColor Green
}

# 创建命名空间
Write-Host "📦 创建命名空间..." -ForegroundColor Blue
kubectl create namespace ringcentral-dev --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace ringcentral-prod --dry-run=client -o yaml | kubectl apply -f -
Write-Host "✅ 命名空间创建完成" -ForegroundColor Green

# 安装NGINX Ingress Controller
Write-Host "🌐 检查NGINX Ingress Controller..." -ForegroundColor Blue
$ingressPods = kubectl get pods -n ingress-nginx --no-headers 2>$null
if (-not $ingressPods) {
    Write-Host "📥 安装NGINX Ingress Controller..." -ForegroundColor Yellow
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
    
    Write-Host "⏳ 等待Ingress Controller启动..." -ForegroundColor Yellow
    kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=300s
    Write-Host "✅ NGINX Ingress Controller安装完成" -ForegroundColor Green
} else {
    Write-Host "✅ NGINX Ingress Controller已存在" -ForegroundColor Green
}

# 配置本地域名解析
Write-Host "🌍 配置本地域名解析..." -ForegroundColor Blue
$hostsFile = "C:\Windows\System32\drivers\etc\hosts"
$domains = @(
    "127.0.0.1 dev-api.ringcentral.local",
    "127.0.0.1 dev-auth.ringcentral.local",
    "127.0.0.1 dev-meeting.ringcentral.local",
    "127.0.0.1 dev-call.ringcentral.local"
)

$hostsContent = Get-Content $hostsFile -ErrorAction SilentlyContinue
$needsUpdate = $false

foreach ($domain in $domains) {
    if ($hostsContent -notcontains $domain) {
        $needsUpdate = $true
        break
    }
}

if ($needsUpdate) {
    Write-Host "📝 更新hosts文件..." -ForegroundColor Yellow
    Write-Host "⚠️  需要管理员权限，请确认UAC提示" -ForegroundColor Yellow
    
    try {
        foreach ($domain in $domains) {
            if ($hostsContent -notcontains $domain) {
                Add-Content -Path $hostsFile -Value $domain -Force
            }
        }
        Write-Host "✅ hosts文件更新完成" -ForegroundColor Green
    } catch {
        Write-Host "❌ 无法更新hosts文件，请以管理员身份运行" -ForegroundColor Red
        Write-Host "💡 或手动添加以下内容到 $hostsFile :" -ForegroundColor Yellow
        foreach ($domain in $domains) {
            Write-Host "   $domain" -ForegroundColor White
        }
    }
} else {
    Write-Host "✅ hosts文件已配置" -ForegroundColor Green
}

# 创建本地镜像仓库Secret（如果需要）
Write-Host "🔐 配置镜像仓库认证..." -ForegroundColor Blue
kubectl create secret docker-registry regcred `
    --docker-server=docker.io `
    --docker-username=ringcentral `
    --docker-password=dummy `
    --docker-email=dev@ringcentral.com `
    --namespace=ringcentral-dev `
    --dry-run=client -o yaml | kubectl apply -f -

Write-Host "✅ 镜像仓库认证配置完成" -ForegroundColor Green

# 显示环境信息
Write-Host ""
Write-Host "🎉 本地Kubernetes环境设置完成!" -ForegroundColor Green
Write-Host ""
Write-Host "📊 环境信息:" -ForegroundColor Cyan
Write-Host "  Kubernetes版本: $(kubectl version --client --short)" -ForegroundColor White
Write-Host "  当前上下文: $(kubectl config current-context)" -ForegroundColor White
Write-Host "  可用命名空间:" -ForegroundColor White
kubectl get namespaces | Select-String "ringcentral"

Write-Host ""
Write-Host "🌐 配置的本地域名:" -ForegroundColor Cyan
foreach ($domain in $domains) {
    $domainName = $domain.Split(' ')[1]
    Write-Host "  https://$domainName" -ForegroundColor White
}

Write-Host ""
Write-Host "🔧 常用命令:" -ForegroundColor Cyan
Write-Host "  查看所有Pod: kubectl get pods -n ringcentral-dev" -ForegroundColor White
Write-Host "  查看服务: kubectl get services -n ringcentral-dev" -ForegroundColor White
Write-Host "  查看Ingress: kubectl get ingress -n ringcentral-dev" -ForegroundColor White
Write-Host "  查看日志: kubectl logs -f deployment/api-gateway -n ringcentral-dev" -ForegroundColor White

Write-Host ""
Write-Host "📚 下一步: 运行 .\scripts\deploy-to-local-k8s.ps1 部署应用" -ForegroundColor Cyan 