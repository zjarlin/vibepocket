package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.plugin.module.dsl.withConfiguration
import site.addzero.vibepocket.di.AppKoinApplication

/**
 * Koin KCP 模式：
 * - @Single 注解在 DatabaseModule 中注册 DataSource / KSqlClient
 * - @KoinApplication 声明 AppKoinApplication
 * - withConfiguration<T>() 是 KCP/KSP 生成的 typed API
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<AppKoinApplication>()
    }
}
