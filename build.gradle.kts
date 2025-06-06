plugins {
    id("jacoco")
}

allprojects {
    group = "com.ringcentral"
    version = "1.0.0"
    
    // 全局依赖解析策略
    configurations.all {
        // 排除所有snakeyaml传递依赖
        exclude(group = "org.yaml", module = "snakeyaml")
        
        resolutionStrategy {
            // 强制使用特定版本，避免Android变体问题
            force("org.yaml:snakeyaml:2.2")
            
            // 依赖替换策略
            dependencySubstitution {
                // 如果遇到Android变体，替换为标准版本
                substitute(module("org.yaml:snakeyaml")).using(module("org.yaml:snakeyaml:2.2"))
            }
            
            // 缓存动态版本
            cacheDynamicVersionsFor(10, "minutes")
            cacheChangingModulesFor(4, "hours")
            
            // 依赖解析失败时的重试策略
            eachDependency {
                if (requested.group == "org.yaml" && requested.name == "snakeyaml") {
                    useVersion("2.2")
                    because("统一snakeyaml版本，避免Android变体问题")
                }
            }
            
            // 排除Android变体
            componentSelection {
                all {
                    if (candidate.group == "org.yaml" && candidate.module == "snakeyaml") {
                        if (candidate.version.contains("android")) {
                            reject("排除Android变体")
                        }
                    }
                }
            }
        }
    }
    
    // 在所有项目中显式添加snakeyaml依赖
    afterEvaluate {
        dependencies {
            if (configurations.findByName("implementation") != null) {
                add("implementation", "org.yaml:snakeyaml:2.2")
            }
            if (configurations.findByName("testImplementation") != null) {
                add("testImplementation", "org.yaml:snakeyaml:2.2")
            }
        }
    }
}

// 自定义任务
tasks.register("buildAll") {
    group = "build"
    description = "构建所有模块"
    dependsOn(subprojects.map { "${it.path}:build" })
}

tasks.register("testAll") {
    group = "verification"
    description = "运行所有测试"
    dependsOn(subprojects.map { "${it.path}:test" })
}

tasks.register("dockerBuildAll") {
    group = "docker"
    description = "构建所有Docker镜像"
    dependsOn(
        ":platform-services:api-gateway:jib",
        ":platform-services:auth-service:jib",
        ":agent-services:meeting-agent:jib",
        ":agent-services:call-agent:jib",
        ":ai-engines:speech-engine:jib",
        ":ai-engines:nlu-engine:jib"
    )
}

// 构建性能监控任务
tasks.register("buildProfile") {
    group = "build"
    description = "构建性能分析"
    doLast {
        println("请手动执行: ./gradlew build --profile --scan")
    }
}

tasks.register("cleanAll") {
    group = "build"
    description = "清理所有模块"
    dependsOn(subprojects.map { "${it.path}:clean" })
}

tasks.register("checkDependencies") {
    group = "verification"
    description = "检查依赖冲突"
    doLast {
        subprojects.forEach { project ->
            println("检查项目: ${project.name}")
            project.configurations.forEach { config ->
                if (config.isCanBeResolved) {
                    try {
                        config.resolvedConfiguration.lenientConfiguration.unresolvedModuleDependencies.forEach { dep ->
                            println("  未解析依赖: ${dep.selector}")
                        }
                    } catch (e: Exception) {
                        // 忽略无法解析的配置
                    }
                }
            }
        }
    }
}