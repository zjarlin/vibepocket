package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import org.koin.dsl.koinConfiguration
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import site.addzero.vibepocket.di.ControllerModule

fun Application.configureKoin() {
    install(Koin, koinConfiguration<ControllerModule> {
        slf4jLogger()
    })
}
