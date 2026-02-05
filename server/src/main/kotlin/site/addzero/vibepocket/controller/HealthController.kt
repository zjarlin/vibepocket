package site.addzero.vibepocket.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single

@Single
class HealthController : Controller {
    override fun register(route: Route) {
        route.get("/health") {
            call.respondText("ok")
        }
    }
}
