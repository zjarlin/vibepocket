package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import site.addzero.vibepocket.di.ControllerModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(ControllerModule().module)
    }
}
