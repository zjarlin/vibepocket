package site.addzero.vibepocket.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single
import site.addzero.vibepocket.Greeting

@Single
class GreetingController : Controller {
    override fun register(route: Route) {
        route.get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
    }
}
