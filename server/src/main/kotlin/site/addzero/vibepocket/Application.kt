package site.addzero.vibepocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import site.addzero.vibepocket.plugins.configureKoin
import site.addzero.vibepocket.plugins.configureRouting
import site.addzero.vibepocket.plugins.configureStatusPages // Import the new plugin

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureRouting()
    configureStatusPages() // Call the new plugin
}
