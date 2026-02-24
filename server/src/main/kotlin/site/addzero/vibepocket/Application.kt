package site.addzero.vibepocket

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.mp.KoinPlatform
import site.addzero.vibepocket.plugins.ioc.generated.iocModule


fun main() {
    ktorApplication().start(wait = true)
    val all = KoinPlatform.getKoin().getAll<Application>()
    println()

}

/**
 * 启动内嵌 Ktor server（非阻塞），返回 server 实例。
 * 桌面端 main.kt 调用此函数，不需要单独部署后端。
 */
fun ktorApplication(
    port: Int = SERVER_PORT,
    host: String = "0.0.0.0",
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    return embeddedServer(Netty, port = port, host = host) {
        iocModule()
    }
}


