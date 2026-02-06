package site.addzero.vibepocket.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single
import site.addzero.network.call.music.MusicSearchClient
import kotlinx.serialization.Serializable // Keep if other Serializable classes are used here
import site.addzero.network.call.music.model.MusicSearchRequest // Import the correct request class
import site.addzero.network.call.music.model.SearchResult // Import the correct result class

@Single
class MusicSearchController : Controller {
    private val client = MusicSearchClient()

    override fun register(route: Route) {
        route.route("/api/music") {
            get("/search") {
                val query = call.request.queryParameters["query"]
                if (query.isNullOrBlank()) {
                    throw IllegalArgumentException("Query parameter 'query' is required.")
                }

                val request = MusicSearchRequest(query) // Use the imported request class
                val results: SearchResult? = client.search(request) // Use the imported result class
                call.respond(results ?: emptyList<Any>()) // Respond with empty list if null, or handle null appropriately
            }
        }
    }
}
