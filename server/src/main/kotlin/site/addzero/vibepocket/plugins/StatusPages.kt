package site.addzero.vibepocket.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
// No need for org.slf4j.LoggerFactory here, use application.log

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondText(text = "400: Bad Request - ${cause.message}", status = HttpStatusCode.BadRequest)
            call.application.log.warn("Bad Request (IllegalArgumentException): ${cause.message}", cause)
        }
        exception<SerializationException> { call, cause ->
            call.respondText(text = "400: Bad Request - Malformed JSON or serialization error: ${cause.message}", status = HttpStatusCode.BadRequest)
            call.application.log.error("Serialization error: ${cause.message}", cause)
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: Internal Server Error - ${cause.message}", status = HttpStatusCode.InternalServerError)
            call.application.log.error("Unhandled exception occurred: ${cause.message}", cause)
        }
    }
}
