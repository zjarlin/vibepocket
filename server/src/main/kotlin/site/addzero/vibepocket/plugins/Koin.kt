package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.plugin.module.dsl.withConfiguration
import site.addzero.ioc.annotation.Bean
import site.addzero.vibepocket.di.AppKoinApplication

/**
 * @Bean是自己封装的DI注解,koin不支持扩展函数的聚合
 */
@Bean
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<AppKoinApplication>()
    }
}
