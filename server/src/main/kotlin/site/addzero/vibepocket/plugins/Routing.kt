package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin
import site.addzero.vibepocket.controller.Controller

fun Application.configureRouting() {
    val controllers = getKoin().getAll<Controller>()
    routing {
        controllers.forEach { it.register(this) }
    }
}
