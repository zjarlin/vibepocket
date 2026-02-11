package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import site.addzero.ioc.annotation.Bean
import site.addzero.vibepocket.plugins.ioc.generated.iocModule

@Bean
fun Application.configureRouting() {
    routing {
       iocModule()
    }
}
