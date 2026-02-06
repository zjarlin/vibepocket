package site.addzero.vibepocket.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single
import site.addzero.network.call.suno.SunoClient
import site.addzero.network.call.suno.model.SunoMusicRequest // Correct request class
import site.addzero.network.call.suno.model.SunoTask // Correct response class (assuming a single SunoTask is returned)
import io.ktor.http.HttpStatusCode // Import HttpStatusCode

@Single
class SunoController : Controller {
    // Assuming SunoClient requires an apiToken.
    // This should ideally be loaded from a secure configuration (e.g., environment variables).
    private val client = SunoClient(apiToken = "YOUR_SUNO_API_TOKEN") // Placeholder API Token

    override fun register(route: Route) {
        route.route("/api/suno") {
            post("/generate") { // Using POST for generation
                val prompt = call.request.queryParameters["prompt"]
                if (prompt.isNullOrBlank()) {
                    throw IllegalArgumentException("Query parameter 'prompt' is required.")
                }

                val request = SunoMusicRequest(prompt = prompt) // Use SunoMusicRequest
                val result = client.generateMusic(request)

                result?.let { audioUrl ->
                    call.respond(mapOf("songUrl" to audioUrl) as Map<String, String>) // Explicitly cast to Map<String, String>
                } ?: throw IllegalStateException("Generated song URL not found.")
            }
        }
    }
}
