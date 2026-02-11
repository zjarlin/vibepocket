package site.addzero.vibepocket.routes

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.koin.ktor.ext.inject
import site.addzero.vibepocket.model.AppConfig
import site.addzero.vibepocket.model.key
import site.addzero.vibepocket.model.value

/**
 * 从 app_config 表读取配置值
 */
fun KSqlClient.getConfig(key: String): String? {
    return createQuery(AppConfig::class) {
        where(table.key eq key)
        select(table.value)
    }.execute().firstOrNull()
}

/**
 * 写入或更新 app_config 配置
 *
 * AppConfig 的 key 字段标注了 @Key，Jimmer save 会自动 upsert。
 */
fun KSqlClient.setConfig(key: String, value: String, description: String? = null) {
    save(
        AppConfig {
            this.key = key
            this.value = value
            this.description = description
        }
    )
}


@Serializable
data class ConfigEntry(val key: String, val value: String, val description: String? = null)

@Serializable
data class ConfigResponse(val key: String, val value: String?)


fun Route.configRoutes() {
    val sqlClient by inject<KSqlClient>()

    route("/api/config") {

        get("/{key}") {
            val key = call.parameters["key"]
                ?: throw IllegalArgumentException("key is required")
            val value = sqlClient.getConfig(key)
            call.respond(ConfigResponse(key = key, value = value))
        }

        put {
            val entry = call.receive<ConfigEntry>()
            sqlClient.setConfig(entry.key, entry.value, entry.description)
            call.respond(mapOf("ok" to true))
        }
    }
}
