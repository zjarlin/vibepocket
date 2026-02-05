package site.addzero.vibepocket.controller

import io.ktor.server.routing.Route

interface Controller {
    fun register(route: Route)
}
