package site.addzero.vibepocket.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import site.addzero.ioc.annotation.Bean

/**
 * 配置 Swagger UI
 *
 * 访问 /swagger 查看交互式 API 文档。
 *
 * 注意: Ktor OpenAPI 编译器插件（openApi { enabled = true }）
 * 与 Kotlin 2.3.20-Beta2 不兼容，已禁用。
 * 当前使用静态 openapi.yaml，后续 Ktor 修复后可重新启用自动推断。
 */
@Bean
fun Application.configureOpenApi() {
    routing {
        swaggerUI("/swagger", "openapi/documentation.yaml")
    }
}
