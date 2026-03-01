package site.addzero.vibepocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.plugin.module.dsl.withConfiguration
import site.addzero.starter.koin.installKoin
import site.addzero.starter.koin.runStarters
import site.addzero.vibepocket.di.AppKoinApplication

/**
 * EngineMain 入口 — Ktor 自动加载 application.conf，
 * 读取 ktor.application.modules 配置调用 Application.module()。
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * 由 application.conf 中 ktor.application.modules 指定调用。
 */
fun Application.module() {
    // 1. Koin 必须最先初始化（发现机制依赖它）
    installKoin {
        withConfiguration<AppKoinApplication>()
    }
    // 2. 自动发现并执行所有 Starter
    runStarters()
    // 3. 路由聚合（iocModule 由 @Bean KSP 生成）
    routing { iocModule() }
}

/**
 * 桌面端内嵌启动入口（非阻塞），返回 server 实例。
 */
fun ktorApplication(
    port: Int = SERVER_PORT,
    host: String = "0.0.0.0",
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    return embeddedServer(Netty, port = port, host = host, module = Application::module)
}
