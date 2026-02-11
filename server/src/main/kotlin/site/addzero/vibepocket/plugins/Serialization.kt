package site.addzero.vibepocket.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        val json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
        json(json)
    }
}
