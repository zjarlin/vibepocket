package site.addzero.vibepocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.annotation.KoinInternalApi
import org.koin.ktor.ext.getKoin
import site.addzero.vibepocket.plugins.configureKoin
import site.addzero.vibepocket.plugins.configureOpenApi
import site.addzero.vibepocket.plugins.configureRouting
import site.addzero.vibepocket.plugins.configureStatusPages


fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}


@OptIn(KoinInternalApi::class)
fun Application.module() {
//    configureBanner()
    configureKoin()
    val koin = getKoin()
    // 如果只是想看有哪些定义（不触发创建）：
    koin.instanceRegistry.instances.forEach { (key, value) ->
        log.info("Bean: $key -> $value")
    }
    configureStatusPages()
    configureRouting()
    configureOpenApi()
}

