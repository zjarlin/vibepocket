package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import site.addzero.vibepocket.routes.configRoutes
import site.addzero.vibepocket.routes.musicRoutes
import site.addzero.vibepocket.routes.sunoRoutes

fun Application.configureRouting() {
    routing {
        sunoRoutes()
        musicRoutes()
        configRoutes()
    }
}
